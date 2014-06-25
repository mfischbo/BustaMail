package de.mfischbo.bustamail.template.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mfischbo.bustamail.template.domain.Template;

public interface TemplateRepositiory extends JpaRepository<Template, UUID> {

}
