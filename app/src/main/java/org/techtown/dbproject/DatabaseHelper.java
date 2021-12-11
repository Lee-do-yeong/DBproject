package org.techtown.dbproject;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Error TAG
    protected static String TAG = "DatabaseHelper";

    private static String databasePath = "C:\\Users\\zeno1\\StudioProjects\\DBproject\\app\\src\\main\\assets"; // 데이터베이스 경로
    private static String databaseName = "toilet.db"; // 데이터베이스 이름
    private static String tableName = "toilet"; // 테이블 이름

    private final Context mContext;
    private SQLiteDatabase mDatabase;

    public DatabaseHelper(Context context){
        super(context, databaseName, null, 1);

        if (Build.VERSION.SDK_INT >= 17){
            databasePath = context.getApplicationInfo().dataDir + "/databases/";
        }
        else {
            databasePath = "/data/data/" + context.getPackageName() + "/databases/";
        }

        this.mContext = context;
    }

    // 데이터베이스 파일 열기
    public boolean OpenDatabaseFile() throws SQLException {

        if(!CheckDatabaseFileExist()){
            CreateDatabase();
        }

        String mPath = databasePath + databaseName;
        try{
            mDatabase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            Log.e(TAG,  "[SUCCESS] " + databaseName + " are Opened");
        }
        catch(SQLException sqlException){
            Log.e(TAG, "[ERROR]" + "Can't Open Database");
        }
        return mDatabase != null;
    }

    // 데이터베이스 파일 존재 여부 확인
    public boolean CheckDatabaseFileExist(){
        File file = new File(databasePath + databaseName);
        return file.exists();
    }

    // Database 만들기
    public void CreateDatabase() throws SQLException{

        this.getReadableDatabase();
        this.close();

        try{
            CopyDatabaseFile();
            Log.e(TAG,  "[SUCCESS] " + databaseName + " are Created");
        }
        catch(IOException ioException){
            // Error Message
            Log.e(TAG, "[ERROR] " + "Unable to create " + databaseName);
            throw new Error(TAG);
        }
    }

    // 데이터베이스 복사
    public void CopyDatabaseFile() throws IOException{

        InputStream inputStream  = mContext.getAssets().open(databaseName);
        String outputFileName = databasePath + databaseName;
        OutputStream outputStream = new FileOutputStream(outputFileName);

        byte[] buffer = new byte[1024];
        int length;
        while((length = inputStream.read(buffer)) > 0){
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }


    // 테이블 정보 가져오기
    public List getTableData() {

        try{
            // 테이블 정보를 저장할 List
            List mList = new ArrayList();

            // 쿼리
            String sql = "SELECT * FROM " + tableName;

            // 테이블 데이터를 읽기 위한 Cursor
            Cursor mCursor = mDatabase.rawQuery(sql, null);

            // 테이블 끝까지 읽기
            if (mCursor != null){

                // 다음 Row로 이동
                while(mCursor.moveToNext()){

                    // 해당 Row 저장
                    Data toilet = new Data();

                    toilet.setId(mCursor.getInt(0));
                    toilet.setType(mCursor.getString(1));
                    toilet.setToilet(mCursor.getString(2));
                    toilet.setAddress(mCursor.getString(3));
                    toilet.setLatitude(mCursor.getDouble(4));
                    toilet.setLongitude(mCursor.getDouble(5));
                    toilet.setNumber(mCursor.getString(6));
                    toilet.setTime(mCursor.getString(7));

                    // List에 해당 Row 추가
                    mList.add(toilet);
                }

            }
            return mList;

        }
        catch (SQLException mSQLException){
            // Error Message
            Log.e(TAG, mSQLException.toString());
            throw mSQLException;
        }

    }

    // 데이터베이스 닫기
    public void CloseDatabaseFile(){
        if (mDatabase != null){
            mDatabase.close();
        }
    }

    @Override
    public synchronized void close(){
        CloseDatabaseFile();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}