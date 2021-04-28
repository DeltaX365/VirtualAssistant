package com.vnsolution.virtualassistant.db;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DbMigration {
    public static final Migration MIGRATION_1_12 = new Migration(1, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };

    public static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };

    public static final Migration MIGRATION_13_14 = new Migration(13, 14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add column Route to Place table
            String addColumnFavouriteToPlaceTableSql = "ALTER TABLE Place ADD COLUMN Favourite INTEGER NOT NULL DEFAULT 0";
            database.execSQL(addColumnFavouriteToPlaceTableSql);
        }
    };
}
