package de.mfischbo.bustamail.mailinglist.domain;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

import de.mfischbo.bustamail.common.domain.OwnedBaseDomain;

@Entity
@Table(name = "SubscriptionList_List")
public class SubscriptionList extends OwnedBaseDomain {

	private static final long serialVersionUID = -5335592314616788305L;

	@Basic
	@Column(nullable = false)
	@NotBlank
	private String			name;
	
	@Basic
	@Column(length = 4095)
	private String			description;
	
	@OneToMany(mappedBy = "subscriptionList")
	private Set<Subscription>	subscriptions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Set<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}
}
