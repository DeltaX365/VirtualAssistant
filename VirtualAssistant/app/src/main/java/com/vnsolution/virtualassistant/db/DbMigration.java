package com.vnsolution.virtualassistant.db;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DbMigration {
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.beginTransaction();

            // Create PhoneCall table
            String phoneCallTableCreationScript = "CREATE TABLE \"PhoneCall\" (\n" +
                    "\t\"CountryIso\"\tTEXT,\n" +
                    "\t\"UpdatedDate\"\tTEXT,\n" +
                    "\t\"BlockReason\"\tINTEGER NOT NULL,\n" +
                    "\t\"CallId\"\tINTEGER NOT NULL,\n" +
                    "\t\"Duration\"\tINTEGER NOT NULL,\n" +
                    "\t\"Presentation\"\tINTEGER NOT NULL,\n" +
                    "\t\"IsNew\"\tINTEGER NOT NULL,\n" +
                    "\t\"GeocodedLocation\"\tTEXT,\n" +
                    "\t\"IsRead\"\tINTEGER NOT NULL,\n" +
                    "\t\"NormalizedNumber\"\tTEXT,\n" +
                    "\t\"Date\"\tINTEGER NOT NULL,\n" +
                    "\t\"LastModified\"\tINTEGER NOT NULL,\n" +
                    "\t\"Type\"\tINTEGER NOT NULL,\n" +
                    "\t\"CreatedDate\"\tTEXT,\n" +
                    "\t\"PhoneNumber\"\tTEXT,\n" +
                    "\t\"Features\"\tINTEGER NOT NULL,\n" +
                    "\t\"Id\"\tTEXT NOT NULL,\n" +
                    "\t\"FormattedNumber\"\tTEXT,\n" +
                    "\tPRIMARY KEY(\"Id\")\n" +
                    ");";
            database.execSQL(phoneCallTableCreationScript);

            // Create PhoneSms table
            String phoneSmsTableCreationScript = "CREATE TABLE \"PhoneSms\" (\n" +
                    "\t\"SmsId\"\tINTEGER NOT NULL,\n" +
                    "\t\"ThreadId\"\tINTEGER NOT NULL,\n" +
                    "\t\"Address\"\tTEXT,\n" +
                    "\t\"Date\"\tINTEGER NOT NULL,\n" +
                    "\t\"DateSent\"\tINTEGER NOT NULL,\n" +
                    "\t\"Protocol\"\tINTEGER NOT NULL,\n" +
                    "\t\"Read\"\tINTEGER NOT NULL,\n" +
                    "\t\"Status\"\tINTEGER NOT NULL,\n" +
                    "\t\"Type\"\tINTEGER NOT NULL,\n" +
                    "\t\"ReplyPathPresent\"\tINTEGER NOT NULL,\n" +
                    "\t\"Subject\"\tTEXT,\n" +
                    "\t\"Body\"\tTEXT,\n" +
                    "\t\"ServiceCenter\"\tTEXT,\n" +
                    "\t\"Locked\"\tINTEGER NOT NULL,\n" +
                    "\t\"ErrorCode\"\tINTEGER NOT NULL,\n" +
                    "\t\"SubId\"\tINTEGER NOT NULL,\n" +
                    "\t\"Creator\"\tTEXT,\n" +
                    "\t\"Seen\"\tINTEGER NOT NULL,\n" +
                    "\t\"Deletable\"\tINTEGER NOT NULL,\n" +
                    "\t\"SimSlot\"\tINTEGER NOT NULL,\n" +
                    "\t\"SimImsi\"\tTEXT,\n" +
                    "\t\"Hidden\"\tINTEGER NOT NULL,\n" +
                    "\t\"GroupId\"\tINTEGER NOT NULL,\n" +
                    "\t\"AppId\"\tINTEGER NOT NULL,\n" +
                    "\t\"MsgId\"\tINTEGER NOT NULL,\n" +
                    "\t\"CallbackNumber\"\tTEXT,\n" +
                    "\t\"Reserved\"\tINTEGER NOT NULL,\n" +
                    "\t\"Pri\"\tINTEGER NOT NULL,\n" +
                    "\t\"TeleserviceId\"\tINTEGER NOT NULL,\n" +
                    "\t\"LinkUrl\"\tTEXT,\n" +
                    "\t\"RoamPending\"\tINTEGER NOT NULL,\n" +
                    "\t\"SpamReport\"\tINTEGER NOT NULL,\n" +
                    "\t\"SecretMode\"\tINTEGER NOT NULL,\n" +
                    "\t\"SafeMessage\"\tINTEGER NOT NULL,\n" +
                    "\t\"Favorite\"\tINTEGER NOT NULL,\n" +
                    "\t\"UsingMode\"\tINTEGER NOT NULL,\n" +
                    "\t\"FromAddress\"\tTEXT,\n" +
                    "\t\"AnnouncementsSubtype\"\tINTEGER NOT NULL,\n" +
                    "\t\"DeviceName\"\tTEXT,\n" +
                    "\t\"CorrelationTag\"\tTEXT,\n" +
                    "\t\"CreatedDate\"\tTEXT,\n" +
                    "\t\"UpdatedDate\"\tTEXT,\n" +
                    "\t\"Id\"\tTEXT NOT NULL,\n" +
                    "\tPRIMARY KEY(\"Id\")\n" +
                    ");";
            database.execSQL(phoneSmsTableCreationScript);

            // Create PhoneContact table
            String phoneContactTableCreationScript = "CREATE TABLE \"PhoneContact\" (\n" +
                    "\t\"ContactId\"\tINTEGER NOT NULL,\n" +
                    "\t\"DisplayName\"\tTEXT,\n" +
                    "\t\"PhoneticName\"\tTEXT,\n" +
                    "\t\"PhoneNumber\"\tTEXT,\n" +
                    "\t\"Pinned\"\tINTEGER NOT NULL,\n" +
                    "\t\"DisplayNameAlt\"\tTEXT,\n" +
                    "\t\"SortKeyAlt\"\tTEXT,\n" +
                    "\t\"InVisibleGroup\"\tINTEGER NOT NULL,\n" +
                    "\t\"Starred\"\tINTEGER NOT NULL,\n" +
                    "\t\"ContactStatusLabel\"\tTEXT,\n" +
                    "\t\"IsUserProfile\"\tINTEGER NOT NULL,\n" +
                    "\t\"HasPhoneNumber\"\tINTEGER NOT NULL,\n" +
                    "\t\"DisplayNameSource\"\tINTEGER NOT NULL,\n" +
                    "\t\"PhoneticNameStyle\"\tINTEGER NOT NULL,\n" +
                    "\t\"Lookup\"\tTEXT,\n" +
                    "\t\"ContactLastUpdatedTimestamp\"\tINTEGER NOT NULL,\n" +
                    "\t\"PhotoUri\"\tTEXT,\n" +
                    "\t\"SortKey\"\tTEXT,\n" +
                    "\t\"InDefaultDirectory\"\tINTEGER NOT NULL,\n" +
                    "\t\"NameRawContactId\"\tINTEGER NOT NULL,\n" +
                    "\t\"CreatedDate\"\tTEXT,\n" +
                    "\t\"UpdatedDate\"\tTEXT,\n" +
                    "\t\"Id\"\tTEXT NOT NULL,\n" +
                    "\tPRIMARY KEY(\"Id\")\n" +
                    ");";
            database.execSQL(phoneContactTableCreationScript);

            // Create PhoneApp table
            String phoneAppTableCreation = "CREATE TABLE \"PhoneApp\" (\n" +
                    "\t\"AppId\"\tINTEGER NOT NULL,\n" +
                    "\t\"AppName\"\tTEXT,\n" +
                    "\t\"PackageName\"\tTEXT,\n" +
                    "\t\"VersionName\"\tTEXT,\n" +
                    "\t\"VersionCode\"\tINTEGER NOT NULL,\n" +
                    "\t\"AppIcon\"\tTEXT,\n" +
                    "\t\"CreatedDate\"\tTEXT,\n" +
                    "\t\"UpdatedDate\"\tTEXT,\n" +
                    "\t\"Id\"\tTEXT NOT NULL,\n" +
                    "\tPRIMARY KEY(\"Id\")\n" +
                    ");";
            database.execSQL(phoneAppTableCreation);

            // Create PhoneAppUsage table
            String phoneAppUsageTableCreation = "CREATE TABLE \"PhoneAppUsage\" (\n" +
                    "\t\"AppId\"\tTEXT,\n" +
                    "\t\"AppLastUsedTime\"\tINTEGER NOT NULL,\n" +
                    "\t\"AppUsageTime\"\tINTEGER NOT NULL,\n" +
                    "\t\"QueryBeginTime\"\tINTEGER NOT NULL,\n" +
                    "\t\"QueryEndTime\"\tINTEGER NOT NULL,\n" +
                    "\t\"CreatedDate\"\tTEXT,\n" +
                    "\t\"UpdatedDate\"\tTEXT,\n" +
                    "\t\"Id\"\tTEXT NOT NULL,\n" +
                    "\tPRIMARY KEY(\"Id\")\n" +
                    ");";
            database.execSQL(phoneAppUsageTableCreation);

//            database.endTransaction();
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Unique index PhoneContact
            String phoneContactDataReset = "DELETE FROM \"PhoneContact\"";
            String phoneContactUniqueIndex = "CREATE UNIQUE INDEX \"index_PhoneContact_ContactId\" ON \"PhoneContact\" (\n" +
                    "\t\"ContactId\"\tASC\n" +
                    ");";
            database.execSQL(phoneContactDataReset);
            database.execSQL(phoneContactUniqueIndex);

            // Unique index PhoneCall
            String phoneCallDataReset = "DELETE FROM PhoneCall";
            String phoneCallUniqueIndex = "CREATE UNIQUE INDEX \"index_PhoneCall_CallId\" ON \"PhoneCall\" (\n" +
                    "\t\"CallId\"\tASC\n" +
                    ");";
            database.execSQL(phoneCallDataReset);
            database.execSQL(phoneCallUniqueIndex);

            // Unique index PhoneSms
            String phoneSmsDataReset = "DELETE FROM PhoneSms";
            String phoneSmsUniqueIndex = "CREATE UNIQUE INDEX \"index_PhoneSms_SmsId\" ON \"PhoneSms\" (\n" +
                    "\t\"SmsId\"\tASC\n" +
                    ");";
            database.execSQL(phoneSmsDataReset);
            database.execSQL(phoneSmsUniqueIndex);

            // Unique index PhoneApp
            String phoneAppDataReset = "DELETE FROM PhoneApp";
            String phoneAppUniqueIndex = "CREATE UNIQUE INDEX \"index_PhoneApp_AppId\" ON \"PhoneApp\" (\n" +
                    "\t\"AppId\"\tASC\n" +
                    ");";
            database.execSQL(phoneAppDataReset);
            database.execSQL(phoneAppUniqueIndex);
        }
    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add column StreetNumber to Place table
            String addColumnStreetNumberToPlaceTableSql = "ALTER TABLE Place ADD COLUMN StreetNumber TEXT";
            database.execSQL(addColumnStreetNumberToPlaceTableSql);

            // Add column Route to Place table
            String addColumnRouteToPlaceTableSql = "ALTER TABLE Place ADD COLUMN Route TEXT";
            database.execSQL(addColumnRouteToPlaceTableSql);

            // Add column AdministrativeAreaLevel3 to Place table
            String addColumnAdministrativeAreaLevel3ToPlaceTableSql = "ALTER TABLE Place ADD COLUMN AdministrativeAreaLevel3 TEXT";
            database.execSQL(addColumnAdministrativeAreaLevel3ToPlaceTableSql);

            // Add column AdministrativeAreaLevel2 to Place table
            String addColumnAdministrativeAreaLevel2ToPlaceTableSql = "ALTER TABLE Place ADD COLUMN AdministrativeAreaLevel2 TEXT";
            database.execSQL(addColumnAdministrativeAreaLevel2ToPlaceTableSql);

            // Add column AdministrativeAreaLevel1 to Place table
            String addColumnAdministrativeAreaLevel1ToPlaceTableSql = "ALTER TABLE Place ADD COLUMN AdministrativeAreaLevel1 TEXT";
            database.execSQL(addColumnAdministrativeAreaLevel1ToPlaceTableSql);

            // Add column Country to Place table
            String addColumnCountryToPlaceTableSql = "ALTER TABLE Place ADD COLUMN Country TEXT";
            database.execSQL(addColumnCountryToPlaceTableSql);
        }
    };

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add column PhotoReference to Place table
            String addColumnPhotoReferenceToPlaceTableSql = "ALTER TABLE Place ADD COLUMN PhotoReference TEXT";
            database.execSQL(addColumnPhotoReferenceToPlaceTableSql);
        }
    };

    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add column Synced to Place table
            String addColumnSyncedToPlaceTableSql = "ALTER TABLE Place ADD COLUMN Synced INTEGER NOT NULL DEFAULT 0";
            database.execSQL(addColumnSyncedToPlaceTableSql);
        }
    };

    public static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add column Synced to Place table
            String addColumnConfirmedToPlaceTableSql = "ALTER TABLE Place ADD COLUMN Confirmed INTEGER NOT NULL DEFAULT 0";
            database.execSQL(addColumnConfirmedToPlaceTableSql);
        }
    };

    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            String reminderTableCreation = "CREATE TABLE Reminder ( " +
                    "Title TEXT, " +
                    "Date TEXT, " +
                    "Repeat TEXT," +
                    "\"Action\" TEXT, " +
                    "ExtendedData TEXT, " +
                    "CreatedDate TEXT, " +
                    "UpdatedDate TEXT, " +
                    "Id TEXT NOT NULL, " +
                    "PRIMARY KEY(Id) " +
                    ");";
            database.execSQL(reminderTableCreation);
        }
    };

    public static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add column AlarmRequestCode to Reminder table
            String addColumnAlarmRequestCodeToReminderTableSql = "ALTER TABLE Reminder ADD COLUMN AlarmRequestCode INTEGER NOT NULL DEFAULT 0";
            database.execSQL(addColumnAlarmRequestCodeToReminderTableSql);
        }
    };

    public static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add column System to PhoneApp table
            String addColumnSystemToPhoneAppTableSql = "ALTER TABLE PhoneApp ADD COLUMN Type INTEGER NOT NULL DEFAULT 0";
            database.execSQL(addColumnSystemToPhoneAppTableSql);
        }
    };
}
