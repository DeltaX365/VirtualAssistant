package com.vnsolution.virtualassistant.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "SmsHistory")
public class SmsHistoryEntity extends AuditEntity {
    @ColumnInfo(name = "ThreadId")
    public String threadId;

    @ColumnInfo(name = "Address")
    public int address;

    @ColumnInfo(name = "Date")
    public long date;

    @ColumnInfo(name = "DateSent")
    public long dateSent;

    @ColumnInfo(name = "Protocol")
    public int protocol;

    @ColumnInfo(name = "IsRead")
    public boolean isRead;

    @ColumnInfo(name = "Status")
    public int status;

    @ColumnInfo(name = "Type")
    public int type;

    @ColumnInfo(name = "ReplyPathPresent")
    public int replyPathPresent;

    @ColumnInfo(name = "Subject")
    public String subject;

    @ColumnInfo(name = "Body")
    public String body;

    @ColumnInfo(name = "ServiceCenter")
    public String serviceCenter;

    @ColumnInfo(name = "IsLocked")
    public boolean isLocked;

    @ColumnInfo(name = "ErrorCode")
    public int errorCode;

    @ColumnInfo(name = "SubId")
    public int subId;

    @ColumnInfo(name = "Creator")
    public String creator;

    @ColumnInfo(name = "IsSeen")
    public boolean isSeen;

    @ColumnInfo(name = "IsDeletable")
    public boolean isDeletable;

    @ColumnInfo(name = "SimSlot")
    public int simSlot;

    @ColumnInfo(name = "SimImsi")
    public String simImsi;

    @ColumnInfo(name = "IsHidden")
    public boolean isHidden;

    @ColumnInfo(name = "GroupId")
    public int groupId;

    @ColumnInfo(name = "AppId")
    public int appId;

    @ColumnInfo(name = "MsgId")
    public int msgId;

    @ColumnInfo(name = "CallbackNumber")
    public String callbackNumber;

    @ColumnInfo(name = "Reserved")
    public int reserved;

    @ColumnInfo(name = "Pri")
    public int pri;

    @ColumnInfo(name = "TeleserviceId")
    public int teleserviceId;

    @ColumnInfo(name = "LinkUrl")
    public String linkUrl;

    @ColumnInfo(name = "RoamPending")
    public int roamPending;

    @ColumnInfo(name = "SpamReport")
    public int spamReport;

    @ColumnInfo(name = "SecretMode")
    public int secretMode;

    @ColumnInfo(name = "SafeMessage")
    public int safeMessage;

    @ColumnInfo(name = "IsFavorite")
    public boolean isFavorite;

    @ColumnInfo(name = "UsingMode")
    public int usingMode;

    @ColumnInfo(name = "FromAddress")
    public String fromAddress;

    @ColumnInfo(name = "AnnouncementsSubtype")
    public int announcementsSubtype;

    @ColumnInfo(name = "DeviceName")
    public String deviceName;

    @ColumnInfo(name = "CorrelationTag")
    public String correlationTag;
}
