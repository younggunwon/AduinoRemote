package younggun.aduinoremote;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by LOVE on 2017-05-28.
 */

public class DBHelper extends SQLiteOpenHelper {

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) { // 새로운 테이블 생성, 생성자에서 받아오지 못할 경우에만 호출됨
        db.execSQL("CREATE TABLE ROBOT (name TEXT, value TEXT);");
        db.execSQL("CREATE TABLE CAR (name TEXT, value TEXT);");
        db.execSQL("CREATE TABLE VARIOUS (name TEXT, value TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수

    }

    public void insert(String tableName, String name, String value) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO "+ tableName +" VALUES('" + name + "', '" + value + "');");
        db.close();
    }

    public void update(String tableName, String name, String value) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 이름과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE " + tableName + " SET value='" + value + "' WHERE name='" + name + "';");
        db.close();
    }

    /*public void delete(String tableName, String name) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 이름과 일치하는 행 삭제
        db.execSQL("DELETE FROM " + tableName +" WHERE name='" + name + "';");
        db.close();
    }*/

    public ArrayList<String> getValue(String tableName) {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> returnList = new ArrayList<String>();

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM "+ tableName, null);
        while (cursor.moveToNext()) {
            returnList.add(cursor.getString(1));
        }
        return returnList;
    }
}