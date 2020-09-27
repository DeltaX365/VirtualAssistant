package com.vnsolution.virtualassistant.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(
    tableName = "PhoneContact",
    indices = {@Index(value = "ContactId", unique = true)}
)
public class PhoneContactEntity extends AuditEntity {
    @ColumnInfo(name = "ContactId")
    public int contactId;

    @ColumnInfo(name = "DisplayName")
    public String displayName;

    @ColumnInfo(name = "PhoneticName")
    public String phoneticName;

    @ColumnInfo(name = "PhoneNumber")
    public String phoneNumbers;

//    @ColumnInfo(name = "IsPrivate")
//    public int isPrivate;

    @ColumnInfo(name = "Pinned")
    public int pinned;

//    @ColumnInfo(name = "LinkCount")
//    public int linkCount;
//
//    @ColumnInfo(name = "Link")
//    public String link;

    @ColumnInfo(name = "DisplayNameAlt")
    public String displayNameAlt;

    @ColumnInfo(name = "SortKeyAlt")
    public String sortKeyAlt;

    @ColumnInfo(name = "InVisibleGroup")
    public int inVisibleGroup;

    @ColumnInfo(name = "Starred")
    public int starred;

    @ColumnInfo(name = "ContactStatusLabel")
    public String contactStatusLabel;

//    @ColumnInfo(name = "PhonebookLabel")
//    public String phonebookLabel;

    @ColumnInfo(name = "IsUserProfile")
    public int isUserProfile;

    @ColumnInfo(name = "HasPhoneNumber")
    public int hasPhoneNumber;

    @ColumnInfo(name = "DisplayNameSource")
    public int displayNameSource;

//    @ColumnInfo(name = "HasEmail")
//    public int hasEmail;

    @ColumnInfo(name = "PhoneticNameStyle")
    public int phoneticNameStyle;

    @ColumnInfo(name = "Lookup")
    public String lookup;

//    @ColumnInfo(name = "PhonebookLabelAlt")
//    public String phonebookLabelAlt;

    @ColumnInfo(name = "ContactLastUpdatedTimestamp")
    public long contactLastUpdatedTimestamp;

    @ColumnInfo(name = "PhotoUri")
    public String photoUri;

//    @ColumnInfo(name = "PhonebookBucket")
//    public int phonebookBucket;

    @ColumnInfo(name = "SortKey")
    public String sortKey;

//    @ColumnInfo(name = "DisplayNameReverse")
//    public String displayNameReverse;

    @ColumnInfo(name = "InDefaultDirectory")
    public int inDefaultDirectory;

//    @ColumnInfo(name = "TimesContacted")
//    public int timesContacted;

//    @ColumnInfo(name = "DirtyContact")
//    public int dirtyContact;

    @ColumnInfo(name = "NameRawContactId")
    public int nameRawContactId;

//    @ColumnInfo(name = "PhonebookBucketAlt")
//    public int phonebookBucketAlt;
}
