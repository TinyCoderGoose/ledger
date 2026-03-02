package com.gxg.ledger.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.gxg.ledger.model.GiftBook;
import com.gxg.ledger.model.GiftRecord;
import com.gxg.ledger.model.ReturnGift;
import com.gxg.ledger.utils.Converters;

@Database(
    entities = {GiftBook.class, GiftRecord.class, ReturnGift.class},
    version = 2,
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static volatile AppDatabase INSTANCE;
    
    public abstract GiftBookDao giftBookDao();
    public abstract GiftRecordDao giftRecordDao();
    public abstract ReturnGiftDao returnGiftDao();
    
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 为gift_books表添加eventType字段
            database.execSQL("ALTER TABLE gift_books ADD COLUMN eventType TEXT DEFAULT '其他'");
            
            // 为gift_records表添加returnNotes字段
            database.execSQL("ALTER TABLE gift_records ADD COLUMN returnNotes TEXT");
        }
    };
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "ledger_database"
                    )
                    .addMigrations(MIGRATION_1_2)
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}