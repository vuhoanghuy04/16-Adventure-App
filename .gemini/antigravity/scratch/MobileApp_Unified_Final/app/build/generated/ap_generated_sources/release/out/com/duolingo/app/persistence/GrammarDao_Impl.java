package com.duolingo.app.persistence;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.duolingo.app.models.GrammarQuestion;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class GrammarDao_Impl implements GrammarDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GrammarQuestion> __insertionAdapterOfGrammarQuestion;

  public GrammarDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGrammarQuestion = new EntityInsertionAdapter<GrammarQuestion>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `grammar_questions` (`id`,`category`,`theoryTitle`,`theoryContent`,`theoryStructure`,`theoryHint`,`questionType`,`questionText`,`optionA`,`optionB`,`optionC`,`optionD`,`correctAnswer`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final GrammarQuestion entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getCategory() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getCategory());
        }
        if (entity.getTheoryTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTheoryTitle());
        }
        if (entity.getTheoryContent() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getTheoryContent());
        }
        if (entity.getTheoryStructure() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getTheoryStructure());
        }
        if (entity.getTheoryHint() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getTheoryHint());
        }
        if (entity.getQuestionType() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getQuestionType());
        }
        if (entity.getQuestionText() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getQuestionText());
        }
        if (entity.getOptionA() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getOptionA());
        }
        if (entity.getOptionB() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getOptionB());
        }
        if (entity.getOptionC() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getOptionC());
        }
        if (entity.getOptionD() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getOptionD());
        }
        if (entity.getCorrectAnswer() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getCorrectAnswer());
        }
      }
    };
  }

  @Override
  public void insertQuestion(final GrammarQuestion question) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfGrammarQuestion.insert(question);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertAll(final List<GrammarQuestion> questions) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfGrammarQuestion.insert(questions);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<GrammarQuestion> getAllQuestions() {
    final String _sql = "SELECT * FROM grammar_questions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfTheoryTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "theoryTitle");
      final int _cursorIndexOfTheoryContent = CursorUtil.getColumnIndexOrThrow(_cursor, "theoryContent");
      final int _cursorIndexOfTheoryStructure = CursorUtil.getColumnIndexOrThrow(_cursor, "theoryStructure");
      final int _cursorIndexOfTheoryHint = CursorUtil.getColumnIndexOrThrow(_cursor, "theoryHint");
      final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
      final int _cursorIndexOfQuestionText = CursorUtil.getColumnIndexOrThrow(_cursor, "questionText");
      final int _cursorIndexOfOptionA = CursorUtil.getColumnIndexOrThrow(_cursor, "optionA");
      final int _cursorIndexOfOptionB = CursorUtil.getColumnIndexOrThrow(_cursor, "optionB");
      final int _cursorIndexOfOptionC = CursorUtil.getColumnIndexOrThrow(_cursor, "optionC");
      final int _cursorIndexOfOptionD = CursorUtil.getColumnIndexOrThrow(_cursor, "optionD");
      final int _cursorIndexOfCorrectAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswer");
      final List<GrammarQuestion> _result = new ArrayList<GrammarQuestion>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final GrammarQuestion _item;
        final String _tmpCategory;
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _tmpCategory = null;
        } else {
          _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
        }
        final String _tmpTheoryTitle;
        if (_cursor.isNull(_cursorIndexOfTheoryTitle)) {
          _tmpTheoryTitle = null;
        } else {
          _tmpTheoryTitle = _cursor.getString(_cursorIndexOfTheoryTitle);
        }
        final String _tmpTheoryContent;
        if (_cursor.isNull(_cursorIndexOfTheoryContent)) {
          _tmpTheoryContent = null;
        } else {
          _tmpTheoryContent = _cursor.getString(_cursorIndexOfTheoryContent);
        }
        final String _tmpTheoryStructure;
        if (_cursor.isNull(_cursorIndexOfTheoryStructure)) {
          _tmpTheoryStructure = null;
        } else {
          _tmpTheoryStructure = _cursor.getString(_cursorIndexOfTheoryStructure);
        }
        final String _tmpTheoryHint;
        if (_cursor.isNull(_cursorIndexOfTheoryHint)) {
          _tmpTheoryHint = null;
        } else {
          _tmpTheoryHint = _cursor.getString(_cursorIndexOfTheoryHint);
        }
        final String _tmpQuestionType;
        if (_cursor.isNull(_cursorIndexOfQuestionType)) {
          _tmpQuestionType = null;
        } else {
          _tmpQuestionType = _cursor.getString(_cursorIndexOfQuestionType);
        }
        final String _tmpQuestionText;
        if (_cursor.isNull(_cursorIndexOfQuestionText)) {
          _tmpQuestionText = null;
        } else {
          _tmpQuestionText = _cursor.getString(_cursorIndexOfQuestionText);
        }
        final String _tmpOptionA;
        if (_cursor.isNull(_cursorIndexOfOptionA)) {
          _tmpOptionA = null;
        } else {
          _tmpOptionA = _cursor.getString(_cursorIndexOfOptionA);
        }
        final String _tmpOptionB;
        if (_cursor.isNull(_cursorIndexOfOptionB)) {
          _tmpOptionB = null;
        } else {
          _tmpOptionB = _cursor.getString(_cursorIndexOfOptionB);
        }
        final String _tmpOptionC;
        if (_cursor.isNull(_cursorIndexOfOptionC)) {
          _tmpOptionC = null;
        } else {
          _tmpOptionC = _cursor.getString(_cursorIndexOfOptionC);
        }
        final String _tmpOptionD;
        if (_cursor.isNull(_cursorIndexOfOptionD)) {
          _tmpOptionD = null;
        } else {
          _tmpOptionD = _cursor.getString(_cursorIndexOfOptionD);
        }
        final String _tmpCorrectAnswer;
        if (_cursor.isNull(_cursorIndexOfCorrectAnswer)) {
          _tmpCorrectAnswer = null;
        } else {
          _tmpCorrectAnswer = _cursor.getString(_cursorIndexOfCorrectAnswer);
        }
        _item = new GrammarQuestion(_tmpCategory,_tmpTheoryTitle,_tmpTheoryContent,_tmpTheoryStructure,_tmpTheoryHint,_tmpQuestionType,_tmpQuestionText,_tmpOptionA,_tmpOptionB,_tmpOptionC,_tmpOptionD,_tmpCorrectAnswer);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<GrammarQuestion> getQuestionsByCategory(final String category) {
    final String _sql = "SELECT * FROM grammar_questions WHERE category = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (category == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, category);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfTheoryTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "theoryTitle");
      final int _cursorIndexOfTheoryContent = CursorUtil.getColumnIndexOrThrow(_cursor, "theoryContent");
      final int _cursorIndexOfTheoryStructure = CursorUtil.getColumnIndexOrThrow(_cursor, "theoryStructure");
      final int _cursorIndexOfTheoryHint = CursorUtil.getColumnIndexOrThrow(_cursor, "theoryHint");
      final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
      final int _cursorIndexOfQuestionText = CursorUtil.getColumnIndexOrThrow(_cursor, "questionText");
      final int _cursorIndexOfOptionA = CursorUtil.getColumnIndexOrThrow(_cursor, "optionA");
      final int _cursorIndexOfOptionB = CursorUtil.getColumnIndexOrThrow(_cursor, "optionB");
      final int _cursorIndexOfOptionC = CursorUtil.getColumnIndexOrThrow(_cursor, "optionC");
      final int _cursorIndexOfOptionD = CursorUtil.getColumnIndexOrThrow(_cursor, "optionD");
      final int _cursorIndexOfCorrectAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswer");
      final List<GrammarQuestion> _result = new ArrayList<GrammarQuestion>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final GrammarQuestion _item;
        final String _tmpCategory;
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _tmpCategory = null;
        } else {
          _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
        }
        final String _tmpTheoryTitle;
        if (_cursor.isNull(_cursorIndexOfTheoryTitle)) {
          _tmpTheoryTitle = null;
        } else {
          _tmpTheoryTitle = _cursor.getString(_cursorIndexOfTheoryTitle);
        }
        final String _tmpTheoryContent;
        if (_cursor.isNull(_cursorIndexOfTheoryContent)) {
          _tmpTheoryContent = null;
        } else {
          _tmpTheoryContent = _cursor.getString(_cursorIndexOfTheoryContent);
        }
        final String _tmpTheoryStructure;
        if (_cursor.isNull(_cursorIndexOfTheoryStructure)) {
          _tmpTheoryStructure = null;
        } else {
          _tmpTheoryStructure = _cursor.getString(_cursorIndexOfTheoryStructure);
        }
        final String _tmpTheoryHint;
        if (_cursor.isNull(_cursorIndexOfTheoryHint)) {
          _tmpTheoryHint = null;
        } else {
          _tmpTheoryHint = _cursor.getString(_cursorIndexOfTheoryHint);
        }
        final String _tmpQuestionType;
        if (_cursor.isNull(_cursorIndexOfQuestionType)) {
          _tmpQuestionType = null;
        } else {
          _tmpQuestionType = _cursor.getString(_cursorIndexOfQuestionType);
        }
        final String _tmpQuestionText;
        if (_cursor.isNull(_cursorIndexOfQuestionText)) {
          _tmpQuestionText = null;
        } else {
          _tmpQuestionText = _cursor.getString(_cursorIndexOfQuestionText);
        }
        final String _tmpOptionA;
        if (_cursor.isNull(_cursorIndexOfOptionA)) {
          _tmpOptionA = null;
        } else {
          _tmpOptionA = _cursor.getString(_cursorIndexOfOptionA);
        }
        final String _tmpOptionB;
        if (_cursor.isNull(_cursorIndexOfOptionB)) {
          _tmpOptionB = null;
        } else {
          _tmpOptionB = _cursor.getString(_cursorIndexOfOptionB);
        }
        final String _tmpOptionC;
        if (_cursor.isNull(_cursorIndexOfOptionC)) {
          _tmpOptionC = null;
        } else {
          _tmpOptionC = _cursor.getString(_cursorIndexOfOptionC);
        }
        final String _tmpOptionD;
        if (_cursor.isNull(_cursorIndexOfOptionD)) {
          _tmpOptionD = null;
        } else {
          _tmpOptionD = _cursor.getString(_cursorIndexOfOptionD);
        }
        final String _tmpCorrectAnswer;
        if (_cursor.isNull(_cursorIndexOfCorrectAnswer)) {
          _tmpCorrectAnswer = null;
        } else {
          _tmpCorrectAnswer = _cursor.getString(_cursorIndexOfCorrectAnswer);
        }
        _item = new GrammarQuestion(_tmpCategory,_tmpTheoryTitle,_tmpTheoryContent,_tmpTheoryStructure,_tmpTheoryHint,_tmpQuestionType,_tmpQuestionText,_tmpOptionA,_tmpOptionB,_tmpOptionC,_tmpOptionD,_tmpCorrectAnswer);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
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
    final String _sql = "SELECT COUNT(*) FROM grammar_questions";
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

  @Override
  public List<GrammarQuestion> getAllCategories() {
    final String _sql = "SELECT * FROM grammar_questions GROUP BY category";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfTheoryTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "theoryTitle");
      final int _cursorIndexOfTheoryContent = CursorUtil.getColumnIndexOrThrow(_cursor, "theoryContent");
      final int _cursorIndexOfTheoryStructure = CursorUtil.getColumnIndexOrThrow(_cursor, "theoryStructure");
      final int _cursorIndexOfTheoryHint = CursorUtil.getColumnIndexOrThrow(_cursor, "theoryHint");
      final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
      final int _cursorIndexOfQuestionText = CursorUtil.getColumnIndexOrThrow(_cursor, "questionText");
      final int _cursorIndexOfOptionA = CursorUtil.getColumnIndexOrThrow(_cursor, "optionA");
      final int _cursorIndexOfOptionB = CursorUtil.getColumnIndexOrThrow(_cursor, "optionB");
      final int _cursorIndexOfOptionC = CursorUtil.getColumnIndexOrThrow(_cursor, "optionC");
      final int _cursorIndexOfOptionD = CursorUtil.getColumnIndexOrThrow(_cursor, "optionD");
      final int _cursorIndexOfCorrectAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswer");
      final List<GrammarQuestion> _result = new ArrayList<GrammarQuestion>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final GrammarQuestion _item;
        final String _tmpCategory;
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _tmpCategory = null;
        } else {
          _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
        }
        final String _tmpTheoryTitle;
        if (_cursor.isNull(_cursorIndexOfTheoryTitle)) {
          _tmpTheoryTitle = null;
        } else {
          _tmpTheoryTitle = _cursor.getString(_cursorIndexOfTheoryTitle);
        }
        final String _tmpTheoryContent;
        if (_cursor.isNull(_cursorIndexOfTheoryContent)) {
          _tmpTheoryContent = null;
        } else {
          _tmpTheoryContent = _cursor.getString(_cursorIndexOfTheoryContent);
        }
        final String _tmpTheoryStructure;
        if (_cursor.isNull(_cursorIndexOfTheoryStructure)) {
          _tmpTheoryStructure = null;
        } else {
          _tmpTheoryStructure = _cursor.getString(_cursorIndexOfTheoryStructure);
        }
        final String _tmpTheoryHint;
        if (_cursor.isNull(_cursorIndexOfTheoryHint)) {
          _tmpTheoryHint = null;
        } else {
          _tmpTheoryHint = _cursor.getString(_cursorIndexOfTheoryHint);
        }
        final String _tmpQuestionType;
        if (_cursor.isNull(_cursorIndexOfQuestionType)) {
          _tmpQuestionType = null;
        } else {
          _tmpQuestionType = _cursor.getString(_cursorIndexOfQuestionType);
        }
        final String _tmpQuestionText;
        if (_cursor.isNull(_cursorIndexOfQuestionText)) {
          _tmpQuestionText = null;
        } else {
          _tmpQuestionText = _cursor.getString(_cursorIndexOfQuestionText);
        }
        final String _tmpOptionA;
        if (_cursor.isNull(_cursorIndexOfOptionA)) {
          _tmpOptionA = null;
        } else {
          _tmpOptionA = _cursor.getString(_cursorIndexOfOptionA);
        }
        final String _tmpOptionB;
        if (_cursor.isNull(_cursorIndexOfOptionB)) {
          _tmpOptionB = null;
        } else {
          _tmpOptionB = _cursor.getString(_cursorIndexOfOptionB);
        }
        final String _tmpOptionC;
        if (_cursor.isNull(_cursorIndexOfOptionC)) {
          _tmpOptionC = null;
        } else {
          _tmpOptionC = _cursor.getString(_cursorIndexOfOptionC);
        }
        final String _tmpOptionD;
        if (_cursor.isNull(_cursorIndexOfOptionD)) {
          _tmpOptionD = null;
        } else {
          _tmpOptionD = _cursor.getString(_cursorIndexOfOptionD);
        }
        final String _tmpCorrectAnswer;
        if (_cursor.isNull(_cursorIndexOfCorrectAnswer)) {
          _tmpCorrectAnswer = null;
        } else {
          _tmpCorrectAnswer = _cursor.getString(_cursorIndexOfCorrectAnswer);
        }
        _item = new GrammarQuestion(_tmpCategory,_tmpTheoryTitle,_tmpTheoryContent,_tmpTheoryStructure,_tmpTheoryHint,_tmpQuestionType,_tmpQuestionText,_tmpOptionA,_tmpOptionB,_tmpOptionC,_tmpOptionD,_tmpCorrectAnswer);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        _result.add(_item);
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
