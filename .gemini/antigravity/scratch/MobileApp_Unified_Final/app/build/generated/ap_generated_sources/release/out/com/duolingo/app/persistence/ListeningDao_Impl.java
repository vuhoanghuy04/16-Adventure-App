package com.duolingo.app.persistence;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.duolingo.app.models.ListeningQuestion;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ListeningDao_Impl implements ListeningDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ListeningQuestion> __insertionAdapterOfListeningQuestion;

  public ListeningDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfListeningQuestion = new EntityInsertionAdapter<ListeningQuestion>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `listening_questions` (`id`,`level`,`audioFile`,`transcript`,`q1Text`,`q1A`,`q1B`,`q1C`,`q1D`,`q1Correct`,`q2Text`,`q2A`,`q2B`,`q2C`,`q2D`,`q2Correct`,`q3Text`,`q3A`,`q3B`,`q3C`,`q3D`,`q3Correct`,`q4Text`,`q4A`,`q4B`,`q4C`,`q4D`,`q4Correct`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ListeningQuestion entity) {
        statement.bindLong(1, entity.id);
        if (entity.level == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.level);
        }
        if (entity.audioFile == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.audioFile);
        }
        if (entity.transcript == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.transcript);
        }
        if (entity.q1Text == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.q1Text);
        }
        if (entity.q1A == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.q1A);
        }
        if (entity.q1B == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.q1B);
        }
        if (entity.q1C == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.q1C);
        }
        if (entity.q1D == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.q1D);
        }
        if (entity.q1Correct == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.q1Correct);
        }
        if (entity.q2Text == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.q2Text);
        }
        if (entity.q2A == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.q2A);
        }
        if (entity.q2B == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.q2B);
        }
        if (entity.q2C == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.q2C);
        }
        if (entity.q2D == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.q2D);
        }
        if (entity.q2Correct == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.q2Correct);
        }
        if (entity.q3Text == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.q3Text);
        }
        if (entity.q3A == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.q3A);
        }
        if (entity.q3B == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.q3B);
        }
        if (entity.q3C == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.q3C);
        }
        if (entity.q3D == null) {
          statement.bindNull(21);
        } else {
          statement.bindString(21, entity.q3D);
        }
        if (entity.q3Correct == null) {
          statement.bindNull(22);
        } else {
          statement.bindString(22, entity.q3Correct);
        }
        if (entity.q4Text == null) {
          statement.bindNull(23);
        } else {
          statement.bindString(23, entity.q4Text);
        }
        if (entity.q4A == null) {
          statement.bindNull(24);
        } else {
          statement.bindString(24, entity.q4A);
        }
        if (entity.q4B == null) {
          statement.bindNull(25);
        } else {
          statement.bindString(25, entity.q4B);
        }
        if (entity.q4C == null) {
          statement.bindNull(26);
        } else {
          statement.bindString(26, entity.q4C);
        }
        if (entity.q4D == null) {
          statement.bindNull(27);
        } else {
          statement.bindString(27, entity.q4D);
        }
        if (entity.q4Correct == null) {
          statement.bindNull(28);
        } else {
          statement.bindString(28, entity.q4Correct);
        }
      }
    };
  }

  @Override
  public void insertAll(final List<ListeningQuestion> questions) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfListeningQuestion.insert(questions);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<ListeningQuestion> getQuestionsByLevel(final String level) {
    final String _sql = "SELECT * FROM listening_questions WHERE level = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (level == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, level);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "level");
      final int _cursorIndexOfAudioFile = CursorUtil.getColumnIndexOrThrow(_cursor, "audioFile");
      final int _cursorIndexOfTranscript = CursorUtil.getColumnIndexOrThrow(_cursor, "transcript");
      final int _cursorIndexOfQ1Text = CursorUtil.getColumnIndexOrThrow(_cursor, "q1Text");
      final int _cursorIndexOfQ1A = CursorUtil.getColumnIndexOrThrow(_cursor, "q1A");
      final int _cursorIndexOfQ1B = CursorUtil.getColumnIndexOrThrow(_cursor, "q1B");
      final int _cursorIndexOfQ1C = CursorUtil.getColumnIndexOrThrow(_cursor, "q1C");
      final int _cursorIndexOfQ1D = CursorUtil.getColumnIndexOrThrow(_cursor, "q1D");
      final int _cursorIndexOfQ1Correct = CursorUtil.getColumnIndexOrThrow(_cursor, "q1Correct");
      final int _cursorIndexOfQ2Text = CursorUtil.getColumnIndexOrThrow(_cursor, "q2Text");
      final int _cursorIndexOfQ2A = CursorUtil.getColumnIndexOrThrow(_cursor, "q2A");
      final int _cursorIndexOfQ2B = CursorUtil.getColumnIndexOrThrow(_cursor, "q2B");
      final int _cursorIndexOfQ2C = CursorUtil.getColumnIndexOrThrow(_cursor, "q2C");
      final int _cursorIndexOfQ2D = CursorUtil.getColumnIndexOrThrow(_cursor, "q2D");
      final int _cursorIndexOfQ2Correct = CursorUtil.getColumnIndexOrThrow(_cursor, "q2Correct");
      final int _cursorIndexOfQ3Text = CursorUtil.getColumnIndexOrThrow(_cursor, "q3Text");
      final int _cursorIndexOfQ3A = CursorUtil.getColumnIndexOrThrow(_cursor, "q3A");
      final int _cursorIndexOfQ3B = CursorUtil.getColumnIndexOrThrow(_cursor, "q3B");
      final int _cursorIndexOfQ3C = CursorUtil.getColumnIndexOrThrow(_cursor, "q3C");
      final int _cursorIndexOfQ3D = CursorUtil.getColumnIndexOrThrow(_cursor, "q3D");
      final int _cursorIndexOfQ3Correct = CursorUtil.getColumnIndexOrThrow(_cursor, "q3Correct");
      final int _cursorIndexOfQ4Text = CursorUtil.getColumnIndexOrThrow(_cursor, "q4Text");
      final int _cursorIndexOfQ4A = CursorUtil.getColumnIndexOrThrow(_cursor, "q4A");
      final int _cursorIndexOfQ4B = CursorUtil.getColumnIndexOrThrow(_cursor, "q4B");
      final int _cursorIndexOfQ4C = CursorUtil.getColumnIndexOrThrow(_cursor, "q4C");
      final int _cursorIndexOfQ4D = CursorUtil.getColumnIndexOrThrow(_cursor, "q4D");
      final int _cursorIndexOfQ4Correct = CursorUtil.getColumnIndexOrThrow(_cursor, "q4Correct");
      final List<ListeningQuestion> _result = new ArrayList<ListeningQuestion>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final ListeningQuestion _item;
        _item = new ListeningQuestion();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfLevel)) {
          _item.level = null;
        } else {
          _item.level = _cursor.getString(_cursorIndexOfLevel);
        }
        if (_cursor.isNull(_cursorIndexOfAudioFile)) {
          _item.audioFile = null;
        } else {
          _item.audioFile = _cursor.getString(_cursorIndexOfAudioFile);
        }
        if (_cursor.isNull(_cursorIndexOfTranscript)) {
          _item.transcript = null;
        } else {
          _item.transcript = _cursor.getString(_cursorIndexOfTranscript);
        }
        if (_cursor.isNull(_cursorIndexOfQ1Text)) {
          _item.q1Text = null;
        } else {
          _item.q1Text = _cursor.getString(_cursorIndexOfQ1Text);
        }
        if (_cursor.isNull(_cursorIndexOfQ1A)) {
          _item.q1A = null;
        } else {
          _item.q1A = _cursor.getString(_cursorIndexOfQ1A);
        }
        if (_cursor.isNull(_cursorIndexOfQ1B)) {
          _item.q1B = null;
        } else {
          _item.q1B = _cursor.getString(_cursorIndexOfQ1B);
        }
        if (_cursor.isNull(_cursorIndexOfQ1C)) {
          _item.q1C = null;
        } else {
          _item.q1C = _cursor.getString(_cursorIndexOfQ1C);
        }
        if (_cursor.isNull(_cursorIndexOfQ1D)) {
          _item.q1D = null;
        } else {
          _item.q1D = _cursor.getString(_cursorIndexOfQ1D);
        }
        if (_cursor.isNull(_cursorIndexOfQ1Correct)) {
          _item.q1Correct = null;
        } else {
          _item.q1Correct = _cursor.getString(_cursorIndexOfQ1Correct);
        }
        if (_cursor.isNull(_cursorIndexOfQ2Text)) {
          _item.q2Text = null;
        } else {
          _item.q2Text = _cursor.getString(_cursorIndexOfQ2Text);
        }
        if (_cursor.isNull(_cursorIndexOfQ2A)) {
          _item.q2A = null;
        } else {
          _item.q2A = _cursor.getString(_cursorIndexOfQ2A);
        }
        if (_cursor.isNull(_cursorIndexOfQ2B)) {
          _item.q2B = null;
        } else {
          _item.q2B = _cursor.getString(_cursorIndexOfQ2B);
        }
        if (_cursor.isNull(_cursorIndexOfQ2C)) {
          _item.q2C = null;
        } else {
          _item.q2C = _cursor.getString(_cursorIndexOfQ2C);
        }
        if (_cursor.isNull(_cursorIndexOfQ2D)) {
          _item.q2D = null;
        } else {
          _item.q2D = _cursor.getString(_cursorIndexOfQ2D);
        }
        if (_cursor.isNull(_cursorIndexOfQ2Correct)) {
          _item.q2Correct = null;
        } else {
          _item.q2Correct = _cursor.getString(_cursorIndexOfQ2Correct);
        }
        if (_cursor.isNull(_cursorIndexOfQ3Text)) {
          _item.q3Text = null;
        } else {
          _item.q3Text = _cursor.getString(_cursorIndexOfQ3Text);
        }
        if (_cursor.isNull(_cursorIndexOfQ3A)) {
          _item.q3A = null;
        } else {
          _item.q3A = _cursor.getString(_cursorIndexOfQ3A);
        }
        if (_cursor.isNull(_cursorIndexOfQ3B)) {
          _item.q3B = null;
        } else {
          _item.q3B = _cursor.getString(_cursorIndexOfQ3B);
        }
        if (_cursor.isNull(_cursorIndexOfQ3C)) {
          _item.q3C = null;
        } else {
          _item.q3C = _cursor.getString(_cursorIndexOfQ3C);
        }
        if (_cursor.isNull(_cursorIndexOfQ3D)) {
          _item.q3D = null;
        } else {
          _item.q3D = _cursor.getString(_cursorIndexOfQ3D);
        }
        if (_cursor.isNull(_cursorIndexOfQ3Correct)) {
          _item.q3Correct = null;
        } else {
          _item.q3Correct = _cursor.getString(_cursorIndexOfQ3Correct);
        }
        if (_cursor.isNull(_cursorIndexOfQ4Text)) {
          _item.q4Text = null;
        } else {
          _item.q4Text = _cursor.getString(_cursorIndexOfQ4Text);
        }
        if (_cursor.isNull(_cursorIndexOfQ4A)) {
          _item.q4A = null;
        } else {
          _item.q4A = _cursor.getString(_cursorIndexOfQ4A);
        }
        if (_cursor.isNull(_cursorIndexOfQ4B)) {
          _item.q4B = null;
        } else {
          _item.q4B = _cursor.getString(_cursorIndexOfQ4B);
        }
        if (_cursor.isNull(_cursorIndexOfQ4C)) {
          _item.q4C = null;
        } else {
          _item.q4C = _cursor.getString(_cursorIndexOfQ4C);
        }
        if (_cursor.isNull(_cursorIndexOfQ4D)) {
          _item.q4D = null;
        } else {
          _item.q4D = _cursor.getString(_cursorIndexOfQ4D);
        }
        if (_cursor.isNull(_cursorIndexOfQ4Correct)) {
          _item.q4Correct = null;
        } else {
          _item.q4Correct = _cursor.getString(_cursorIndexOfQ4Correct);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int getListeningCount() {
    final String _sql = "SELECT COUNT(*) FROM listening_questions";
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
