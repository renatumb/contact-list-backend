package org.example.contact_list_backend.repo;

import java.util.Optional;
import org.example.contact_list_backend.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepo extends JpaRepository<Contact, String> {

    Optional<Contact> findById(String id);

    Optional<Contact> findByEmail(String email);


}
