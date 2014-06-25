package de.mfischbo.bustamail.security.domain;

import java.util.Collection;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.common.domain.Gender;
import de.mfischbo.bustamail.common.domain.PersonalizedEmailRecipient;

@Entity
@Table(name = "Security_User")
public class User extends BaseDomain implements UserDetails, PersonalizedEmailRecipient {

	private static final long serialVersionUID = 6348512582303776397L;

	@Basic
	private String firstName;
	
	@Basic
	private String lastName;
	
	@Basic
	@Enumerated(EnumType.STRING)
	private Gender	gender;
	
	@Basic
	@NotBlank
	@Email
	@Column(name = "email", nullable = false, unique = true)
	private String email;
	
	@Basic
	@NotBlank
	@Column(name = "password", nullable = false)
	private String password;
	
	@Basic
	@NotNull
	@Column(name = "dateCreated", nullable = false)
	private DateTime	dateCreated;
	
	@Basic
	@NotNull
	@Column(name = "dateModified", nullable = false)
	private DateTime	dateModified;
	
	@Basic
	private boolean		locked;
	
	@Basic
	private boolean		deleted;
	
	@Basic
	private boolean		hidden;

	@ManyToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private Set<Actor>		actors;
	
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public DateTime getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}
	public DateTime getDateModified() {
		return dateModified;
	}
	public void setDateModified(DateTime dateModified) {
		this.dateModified = dateModified;
	}
	public Set<Actor> getActors() {
		return actors;
	}
	public void setActors(Set<Actor> actors) {
		this.actors = actors;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	@Transient
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Transient
	@Override
	public String getUsername() {
		return email;
	}

	@Transient
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Transient
	@Override
	public boolean isAccountNonLocked() {
		return !locked;
	}

	@Transient
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Transient
	@Override
	public boolean isEnabled() {
		return !locked;
	}
	@Override
	public String toString() {
		return "User [email=" + email + ", id=" + id + "]";
	}
	@Override
	public boolean hasFormalSalutation() {
		return true;
	}
}
