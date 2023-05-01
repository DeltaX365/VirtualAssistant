package vn.sttechnology.virtualassistant.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(
    tableName = "PhoneSms",
    indices = {@Index(value = "SmsId", unique = true)}
)
public class PhoneSmsEntity extends AuditEntity {
    @ColumnInfo(name = "SmsId")
    public int smsId;

    @ColumnInfo(name = "ThreadId")
    public int threadId;

    @ColumnInfo(name = "Address")
    public String address;

    @ColumnInfo(name = "Date")
    public long date;

    @ColumnInfo(name = "DateSent")
    public long dateSent;

    @ColumnInfo(name = "Protocol")
    public int protocol;

    @ColumnInfo(name = "Read")
    public int read;

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

    @ColumnInfo(name = "Locked")
    public int locked;

    @ColumnInfo(name = "ErrorCode")
    public int errorCode;

    @ColumnInfo(name = "SubId")
    public int subId;

    @ColumnInfo(name = "Creator")
    public String creator;

    @ColumnInfo(name = "Seen")
    public int seen;

    @ColumnInfo(name = "Deletable")
    public int deletable;

    @ColumnInfo(name = "SimSlot")
    public int simSlot;

    @ColumnInfo(name = "SimImsi")
    public String simImsi;

    @ColumnInfo(name = "Hidden")
    public int hidden;

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

    @ColumnInfo(name = "Favorite")
    public int favorite;

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
