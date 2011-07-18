package com.enonic.cms.admin.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 7/18/11
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserInfoModel
{
    private String firstName;

    private String lastName;

    private String middleName;

    private Date birthday;

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

    private List<AddressModel> addresses = new ArrayList<AddressModel>(  );

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday( Date birthday )
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

    public String getGlobalPosition()
    {
        return globalPosition;
    }

    public void setGlobalPosition( String globalPosition )
    {
        this.globalPosition = globalPosition;
    }

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

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName( String nickName )
    {
        this.nickName = nickName;
    }

    public String getPersonalId()
    {
        return personalId;
    }

    public void setPersonalId( String personalId )
    {
        this.personalId = personalId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId( String memberId )
    {
        this.memberId = memberId;
    }

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
}
