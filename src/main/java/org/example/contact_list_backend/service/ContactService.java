package org.example.contact_list_backend.service;

import jakarta.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.example.contact_list_backend.domain.Contact;
import org.example.contact_list_backend.repo.ContactRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.example.contact_list_backend.constant.Constant.PHOTO_DIRECTORY;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepo contactRepo;

    public Page<Contact> getAllContacts(int page, int size) {
        return contactRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    public Contact getContact(String id) {
        return contactRepo.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    public Contact createContact(Contact c) {
        return contactRepo.save(c);
    }

    public void deleteContact(String id) {
        contactRepo.delete(contactRepo.findById(id).orElseThrow(() -> new RuntimeException("Contact not found")));
    }

    public String uploadPhoto(String id, MultipartFile multipartFile) {
        Contact contact = getContact(id);
        String photoUrl = photoFunction.apply(id, multipartFile);
        contact.setPhotoUrl(photoUrl);
        contactRepo.save(contact);

        return photoUrl;
    }

    private final Function<String, String> fileExtension = (filename) -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String fileName = id + fileExtension.apply(image.getOriginalFilename());

        try {
            Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(fileName), REPLACE_EXISTING);

            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/contacts/image/" + fileName).toUriString();
        } catch (Exception ex) {
            throw new RuntimeException("unable to save image");
        }
    };
}

