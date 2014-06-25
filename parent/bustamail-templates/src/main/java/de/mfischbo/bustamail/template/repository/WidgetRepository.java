package de.mfischbo.bustamail.template.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.mfischbo.bustamail.template.domain.Widget;

public interface WidgetRepository extends JpaRepository<Widget, UUID> {

}
