package com.enonic.cms.admin.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.codehaus.jackson.annotate.JsonProperty;

import com.enonic.cms.domain.user.Gender;

public class UserInfoModel
{

    private String firstName;


    private String lastName;


    private String middleName;

    private String birthday;

    private String country;

    private String description;

    private String initials;

    private String globalPosition;

    private Boolean htmlEmail;

    private Locale locale;

    private String nickName;

    private String personalId;

    private String memberId;

    private String organization;

    private String prefix;

    private String suffix;

    private String title;

    private String homePage;

    private String mobile;

    private String phone;

    private String fax;

    private TimeZone timeZone;

    private Gender gender;

    private List<AddressModel> addresses = new ArrayList<AddressModel>();

    @JsonProperty("first-name")
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    @JsonProperty("last-name")
    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    @JsonProperty("middle-name")
    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }

    public String getBirthday()
    {
        return birthday;
    }

    public void setBirthday( String birthday )
    {
        this.birthday = birthday;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry( String country )
    {
        this.country = country;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getInitials()
    {
        return initials;
    }

    public void setInitials( String initials )
    {
        this.initials = initials;
    }

    @JsonProperty("global-position")
    public String getGlobalPosition()
    {
        return globalPosition;
    }

    public void setGlobalPosition( String globalPosition )
    {
        this.globalPosition = globalPosition;
    }

    @JsonProperty("html-email")
    public Boolean getHtmlEmail()
    {
        return htmlEmail;
    }

    public void setHtmlEmail( Boolean htmlEmail )
    {
        this.htmlEmail = htmlEmail;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale( Locale locale )
    {
        this.locale = locale;
    }

    @JsonProperty("nick-name")
    public String getNickName()
    {
        return nickName;
    }

    public void setNickName( String nickName )
    {
        this.nickName = nickName;
    }

    @JsonProperty("personal-id")
    public String getPersonalId()
    {
        return personalId;
    }

    public void setPersonalId( String personalId )
    {
        this.personalId = personalId;
    }

    @JsonProperty("member-id")
    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId( String memberId )
    {
        this.memberId = memberId;
    }

    @JsonProperty("organization")
    public String getOrganization()
    {
        return organization;
    }

    public void setOrganization( String organization )
    {
        this.organization = organization;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix( String prefix )
    {
        this.prefix = prefix;
    }

    public String getSuffix()
    {
        return suffix;
    }

    public void setSuffix( String suffix )
    {
        this.suffix = suffix;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    @JsonProperty("home-page")
    public String getHomePage()
    {
        return homePage;
    }

    public void setHomePage( String homePage )
    {
        this.homePage = homePage;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile( String mobile )
    {
        this.mobile = mobile;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone( String phone )
    {
        this.phone = phone;
    }

    public String getFax()
    {
        return fax;
    }

    public void setFax( String fax )
    {
        this.fax = fax;
    }

    @JsonProperty("timezone")
    public TimeZone getTimeZone()
    {
        return timeZone;
    }

    public void setTimeZone( TimeZone timeZone )
    {
        this.timeZone = timeZone;
    }

    public List<AddressModel> getAddresses()
    {
        return addresses;
    }

    public void setAddresses( List<AddressModel> addresses )
    {
        this.addresses = addresses;
    }

    public Gender getGender()
    {
        return gender;
    }

    public void setGender( Gender gender )
    {
        this.gender = gender;
    }
}
