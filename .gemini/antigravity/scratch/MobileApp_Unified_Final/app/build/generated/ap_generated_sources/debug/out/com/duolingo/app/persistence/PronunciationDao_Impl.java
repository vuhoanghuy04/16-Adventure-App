package com.duolingo.app.persistence;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.duolingo.app.models.PronunciationQuestion;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class PronunciationDao_Impl implements PronunciationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PronunciationQuestion> __insertionAdapterOfPronunciationQuestion;

  public PronunciationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPronunciationQuestion = new EntityInsertionAdapter<PronunciationQuestion>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `pronunciation_questions` (`id`,`lesson_id`,`transcript`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final PronunciationQuestion entity) {
        statement.bindLong(1, entity.id);
        if (entity.lessonId == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.lessonId);
        }
        if (entity.transcript == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.transcript);
        }
      }
    };
  }

  @Override
  public void insertAll(final List<PronunciationQuestion> questions) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfPronunciationQuestion.insert(questions);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<PronunciationQuestion> getQuestionsByLessonId(final String lessonId) {
    final String _sql = "SELECT * FROM pronunciation_questions WHERE lesson_id = ? LIMIT 5";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (lessonId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, lessonId);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfLessonId = CursorUtil.getColumnIndexOrThrow(_cursor, "lesson_id");
      final int _cursorIndexOfTranscript = CursorUtil.getColumnIndexOrThrow(_cursor, "transcript");
      final List<PronunciationQuestion> _result = new ArrayList<PronunciationQuestion>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final PronunciationQuestion _item;
        final String _tmpLessonId;
        if (_cursor.isNull(_cursorIndexOfLessonId)) {
          _tmpLessonId = null;
        } else {
          _tmpLessonId = _cursor.getString(_cursorIndexOfLessonId);
        }
        final String _tmpTranscript;
        if (_cursor.isNull(_cursorIndexOfTranscript)) {
          _tmpTranscript = null;
        } else {
          _tmpTranscript = _cursor.getString(_cursorIndexOfTranscript);
        }
        _item = new PronunciationQuestion(_tmpLessonId,_tmpTranscript);
        _item.id = _cursor.getInt(_cursorIndexOfId);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int getCount() {
    final String _sql = "SELECT COUNT(id) FROM pronunciation_questions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
