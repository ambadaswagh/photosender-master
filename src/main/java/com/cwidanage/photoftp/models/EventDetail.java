package com.cwidanage.photoftp.models;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Chathura Widanage
 */
@Entity
@Table(name = "event")
public class EventDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	@Column(name = "description")
	String description;
	@Column(name = "code", unique = true)
	private String eventCode;
	@Column(name = "name")
	String name;
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "destination")
	private FTPGroup destination;
	@Column(name = "City")
	String city;
	@Column(name = "State")
	String state;     
	@Column(name = "Country")
	String country;   
	@Column(name = "Category")
	String category;
	@Column(name = "AuthorsPosition")
	String authorsPosition;
	@Column(name = "Credit") 
	String credit;
	@Column(name = "Source")
	String source;
	@Column(name = "CaptionWriter")
	String captionWriter;
	@Column(name = "Headline")
	String headline;
	@Column(name = "Instructions", length = 355)
	String instructions;
	@Column(name = "WebStatement")
	String webStatement;
	@Column(name = "CountryCode")
	String countryCode;
	@Column(name = "Location")
	String location;
	@Column(name = "subcat")
	String subcat;
	@Column(name = "subcat1")
	String subcat1;  
	@Column(name = "subcat2")
	String subcat2; 
	@Column(name = "UsageTerms", length = 355)
	String usageTerms; 
	@Column(name = "subject", length = 500)
	String subject;
	@Column(name = "creator")
	String creator;
	@Column(name = "title")
	String title;
	@Column(name = "rights")
	String rights;  
	@Column(name = "SubjectCode")
	String subjectCode;
	@Column(name = "CiAdrExtadr")
	String ciAdrExtadr;
	@Column(name = "CiAdrCity")
	String ciAdrCity; 
	@Column(name = "CiAdrRegion")
	String ciAdrRegion;
	@Column(name = "CiAdrPcode")
	String ciAdrPcode; 
	@Column(name = "CiAdrCtry")
	String ciAdrCtry;
	@Column(name = "CiTelWork")
	String ciTelWork;   
	@Column(name = "CiEmailWork")
	String ciEmailWork;
	@Column(name = "CiUrlWork")
	String ciUrlWork;
	@Column(name = "pemail")
	String pemail;
	@Column(name = "EventDate")
	private Long eventDate;
	@Column(name = "filename")
	String filename;
	@Column(name = "ProcessedPhotosCount")
	private Integer processedPhotosCount;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEventCode() {
		return eventCode;
	}
	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public FTPGroup getDestination() {
		return destination;
	}
	public void setDestination(FTPGroup destination) {
		this.destination = destination;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getAuthorsPosition() {
		return authorsPosition;
	}
	public void setAuthorsPosition(String authorsPosition) {
		this.authorsPosition = authorsPosition;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getCaptionWriter() {
		return captionWriter;
	}
	public void setCaptionWriter(String captionWriter) {
		this.captionWriter = captionWriter;
	}
	public String getHeadline() {
		return headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	public String getWebStatement() {
		return webStatement;
	}
	public void setWebStatement(String webStatement) {
		this.webStatement = webStatement;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getSubcat() {
		return subcat;
	}
	public void setSubcat(String subcat) {
		this.subcat = subcat;
	}
	public String getSubcat1() {
		return subcat1;
	}
	public void setSubcat1(String subcat1) {
		this.subcat1 = subcat1;
	}
	public String getSubcat2() {
		return subcat2;
	}
	public void setSubcat2(String subcat2) {
		this.subcat2 = subcat2;
	}
	public String getUsageTerms() {
		return usageTerms;
	}
	public void setUsageTerms(String usageTerms) {
		this.usageTerms = usageTerms;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	public String getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}
	public String getCiAdrExtadr() {
		return ciAdrExtadr;
	}
	public void setCiAdrExtadr(String ciAdrExtadr) {
		this.ciAdrExtadr = ciAdrExtadr;
	}
	public String getCiAdrCity() {
		return ciAdrCity;
	}
	public void setCiAdrCity(String ciAdrCity) {
		this.ciAdrCity = ciAdrCity;
	}
	public String getCiAdrRegion() {
		return ciAdrRegion;
	}
	public void setCiAdrRegion(String ciAdrRegion) {
		this.ciAdrRegion = ciAdrRegion;
	}
	public String getCiAdrPcode() {
		return ciAdrPcode;
	}
	public void setCiAdrPcode(String ciAdrPcode) {
		this.ciAdrPcode = ciAdrPcode;
	}
	public String getCiAdrCtry() {
		return ciAdrCtry;
	}
	public void setCiAdrCtry(String ciAdrCtry) {
		this.ciAdrCtry = ciAdrCtry;
	}
	public String getCiTelWork() {
		return ciTelWork;
	}
	public void setCiTelWork(String ciTelWork) {
		this.ciTelWork = ciTelWork;
	}
	public String getCiEmailWork() {
		return ciEmailWork;
	}
	public void setCiEmailWork(String ciEmailWork) {
		this.ciEmailWork = ciEmailWork;
	}
	public String getCiUrlWork() {
		return ciUrlWork;
	}
	public void setCiUrlWork(String ciUrlWork) {
		this.ciUrlWork = ciUrlWork;
	}
	public String getPemail() {
		return pemail;
	}
	public void setPemail(String pemail) {
		this.pemail = pemail;
	}
	public void setEventDate(Long eventDate) {
		this.eventDate = eventDate;
	}
	public Long getEventDate() {
		return eventDate;
	}
	public Integer getProcessedPhotosCount() {
		return processedPhotosCount;
	}
	public void setProcessedPhotosCount(Integer processedPhotosCount) {
		this.processedPhotosCount = processedPhotosCount;
	}
	public String getfilename() {
		return filename;
	}
	public void setfilename(String filename) {
		this.filename = filename;
	}
}