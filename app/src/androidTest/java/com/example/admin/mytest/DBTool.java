package com.example.admin.mytest;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// 服务器：用户代码对应UserId,业务员ID对应SaleManId，，，客户ID对应ClientId,客户编码对应ClientCode
public class DBTool extends SQLiteOpenHelper {
    static String TAG = "SQLiteOpenHelper";

    private final static String DBName = "/data/data/com.example.admin.mytest/databases/" + "com.zto.pdaunity";
    private static DBTool dbTool = null;
    Context context;

    // 上一次发布6-16的版本是7
    // 7-27 版本8
    // 09-06 版本9
    // 10-15 版本10----（软件Version：16）
    // 11-18 版本11
    // 12-18 版本12
    // 2016-01-08 版本13
    private static final int VERSION_NOW = 13;

    private DBTool(Context context) {
        super(context, DBName, null, VERSION_NOW);
        this.context = context;
        Log.d(TAG, "---new TOOL");
    }

    public static DBTool getInstance(Context context) {

        if (dbTool == null) {

            dbTool = new DBTool(context);
        }
        return dbTool;
    }

    public DBTool(Context context, String name, CursorFactory factory,
                  int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "--来到生成database");
        try {
            createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalAccessError("出现了爆炸问题");
        }

//        doCreateTable(db);
//        doCreateIndex(db);
//
//        // 有些用户删除了数据库，，重新建立额外的表
//        onUpgrade(db, 0, VERSION_NOW);
    }

    private void createDatabase() throws IOException {
        InputStream is = context.getAssets().open("com.zto.pdaunity");
        OutputStream os = new FileOutputStream(new File(DBName));

        Log.d(TAG, "开始写");
        byte[] buffer = new byte[1024];
        for (int length = is.read(buffer); length > 0; ) {
            os.write(buffer, 0, length);
            length = is.read(buffer);
        }

        is.close();
        os.close();
    }

    // <a>营业厅编号代码</a>
    // <b>营业厅名称</b>
    // <c>网点编号</c>
    // <d>网点名称</d>
    // <e>审核时间</e>
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 6 && newVersion >= 6) {
            try {

                db.execSQL("CREATE TABLE tmsTransitCompanValues" + "("
                        + "[ID] INTEGER NOT NULL PRIMARY KEY,"
                        + "[CODE] NVARCHAR(20) NOT NULL,"
                        + "[NAME] NVARCHAR(20) NOT NULL,"
                        + "[SITENUMBER] NVARCHAR(20) NOT NULL,"
                        + "[SITECODE] NVARCHAR(20) NOT NULL,"
                        + "[TIME] NVARCHAR(20) NOT NULL)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (oldVersion < 7 && newVersion >= 7) {

            // 修改员工表
            modifyUserProfile(db);
            // 创建客户资料表
            db.execSQL("CREATE TABLE tmsClient"
                    + "("
                    + "[ID]                  INTEGER         NOT NULL    PRIMARY KEY,"
                    + "[ClientId]            NVARCHAR(20)    NOT NULL    DEFAULT '',"
                    + "[ClientCode]          NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[ClientName]          NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[LastModifyDate]      NVARCHAR(100)   NULL        DEFAULT ''"
                    + ")");
            // 修改扫描数据表
            modifyScanData(db);
        }
        if (oldVersion < 8 && newVersion >= 8) {
            // 修复上次更新的失误（没有为扫描数据添加索引）
            doCreateIndexScan(db);
        }

        if (oldVersion < 9 && newVersion >= 9) {
            // 以前的失误：两次获取数据，可能把第一次的数据写到了第二次对应的表中(后台下载类：LoginDowanloadValuesAsyncTask)
            // 原因：xmlParsing（String）方法中，全局变量responseEntity没有进行初始化
            // 解决办法：将G003后面的数据表，全部清空，再次获得新鲜的数据
            clearMatterTable(db);
        }

        if (oldVersion < 10 && newVersion >= 10) {
            // 新加了自动分拣，需要保存分拣口信息
            // 创建分拣口表
            db.execSQL("CREATE TABLE tmsAutoSortPort"
                    + "("
                    + "[ID]                  INTEGER         NOT NULL    PRIMARY KEY,"
                    + "[SiteName]            NVARCHAR(20)    NOT NULL    DEFAULT '',"
                    + "[SiteId]              NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[PortCode]            NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[PortName]            NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[LastModifyDate]      NVARCHAR(100)   NULL        DEFAULT ''"
                    + ")");
        }

        if (oldVersion < 11 && newVersion >= 11) {
            // 自动分拣需要加个字段（是否是同一省份）
            db.execSQL("DELETE FROM tmsAutoSortPort");
            db.execSQL("ALTER TABLE tmsAutoSortPort ADD [SameProvince] INTEGER DEFAULT 1");
        }

        if (oldVersion < 12 && newVersion >= 12) {
            // 自动分拣再次添加字段（分拣线编号）
            db.execSQL("DELETE FROM tmsAutoSortPort");
            db.execSQL("ALTER TABLE tmsAutoSortPort ADD [SortLineCode] NVARCHAR(20) DEFAULT ''");
        }

        if (oldVersion < 13 && newVersion >= 13) {
            // 增加拦截件数据表(最后修改时间和拦截件登记时间一致)
            db.execSQL("CREATE TABLE tmsRejection"
                    + "("
                    + "[ID]                  INTEGER         NOT NULL    PRIMARY KEY,"
                    + "[ScanHawb]            NVARCHAR(20)    NOT NULL    DEFAULT '',"
                    + "[LastModifyDate]      NVARCHAR(100)   NULL        DEFAULT '',"
                    + "[SiteName]            NVARCHAR(20)    NULL        DEFAULT ''"
                    + ")");
            db.execSQL("CREATE INDEX idx_tmsRejection_ScanHawb on tmsRejection(ScanHawb)");
        }

    }

    // 修改员工信息表（添加字段：是否验证过手机，密码密文,业务员ID）
    private void modifyUserProfile(SQLiteDatabase db) {
        // <-----------------------不保存原始数据--------------------------->

        // 删除旧表
        db.execSQL("drop table tmsUserProfile");
        // 创建新表
        db.execSQL("CREATE TABLE tmsUserProfile"
                + "("
                + "[ID]                INTEGER         NOT NULL    PRIMARY KEY,"
                + "[UserId]            NVARCHAR(20)    NOT NULL    DEFAULT '',"
                + "[UserName]          NVARCHAR(40)    NULL        DEFAULT '',"
                + "[Tel]               NVARCHAR(30)    NULL        DEFAULT '',"
                + "[Password]          NVARCHAR(20)    NULL        DEFAULT '',"
                + "[StationCode]       NVARCHAR(20)    NULL        DEFAULT '',"
                + "[StationName]       NVARCHAR(50)    NULL        DEFAULT '',"
                + "[UserType]          INT             NULL        DEFAULT 3,"
                + "[AdminFlag]         BIT             NULL        DEFAULT 'FALSE',"
                + "[LastModifyUser]    NVARCHAR(20)    NULL        DEFAULT '',"
                + "[LastModifyDate]    NVARCHAR(100)   NULL        DEFAULT '',"
                + "[Verified]     	   INT             NULL        DEFAULT 0,"
                + "[EncryPsw]          NVARCHAR(100)   NULL        DEFAULT '',"
                + "[SaleManId]         NVARCHAR(100)   NULL        DEFAULT ''"
                + ")");

        // <----------------------保存原始数据-------------------------->
        // // 重命名为临时表
        // db.execSQL("alter table tmsUserProfile rename to _tmsUserProfile");
        // // 创建新表
        // db.execSQL("CREATE TABLE tmsUserProfile"
        // + "("
        // + "[ID]                INTEGER         NOT NULL    PRIMARY KEY,"
        // + "[UserId]            NVARCHAR(20)    NOT NULL    DEFAULT '',"
        // + "[UserName]          NVARCHAR(40)    NULL        DEFAULT '',"
        // + "[Tel]               NVARCHAR(30)    NULL        DEFAULT '',"
        // + "[Password]          NVARCHAR(20)    NULL        DEFAULT '',"
        // + "[StationCode]       NVARCHAR(20)    NULL        DEFAULT '',"
        // + "[StationName]       NVARCHAR(50)    NULL        DEFAULT '',"
        // + "[UserType]          INT             NULL        DEFAULT 3,"
        // + "[AdminFlag]         BIT             NULL        DEFAULT 'FALSE',"
        // + "[LastModifyUser]    NVARCHAR(20)    NULL        DEFAULT '',"
        // + "[LastModifyDate]    NVARCHAR(100)   NULL        DEFAULT '',"
        // + "[Verified]     	   INT             NULL        DEFAULT 0,"
        // + "[EncryPsw]          NVARCHAR(100)   NULL        DEFAULT '',"
        // + "[SaleManId]         NVARCHAR(100)   NULL        DEFAULT ''"
        // + ")");
        // // 填充数据
        // db.execSQL("insert into tmsUserProfile select *,'0','','' from _tmsUserProfile");
        // // 删除临时表
        // db.execSQL("drop table _tmsUserProfile");
    }

    // 修改扫描数据表（保存数据）
    private void modifyScanData(SQLiteDatabase db) {

        try {
            db.beginTransaction();
            // 重命名为临时表
            db.execSQL("alter table tmsScanData rename to _tmsScanData");
            // 创建新表(添加客户ID标签)
            db.execSQL("CREATE TABLE tmsScanData"
                    + "("
                    + "[Id]                INTEGER         NOT NULL    PRIMARY KEY,"
                    + "[ScanBatch]         NVARCHAR(20)    NOT NULL    DEFAULT '',"
                    + "[FromToStation]     NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[BusinessMan]       NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[ScanType]          INT             NOT NULL    DEFAULT 10,"
                    + "[ScanHawb]          NVARCHAR(20)    NOT NULL    DEFAULT '',        --单号(也可作为自动分拣的分拣口号)\r\n"
                    + "[ScanCarcode]       NVARCHAR(20)    NOT NULL    DEFAULT '',        --车牌号码\r\n"
                    + "[BagNumber]         NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[ScanUser]          NVARCHAR(20)    NOT NULL    DEFAULT '',"
                    + "[ScanTime]          Datetime        NOT NULL    DEFAULT (GETDATE()),"
                    + "[OperationTime]     Datetime        NULL        DEFAULT (GETDATE()),"
                    + "[ShiftTimes]        NVARCHAR(10)    NULL        DEFAULT '',"
                    + "[ScanStation]       NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[ExceptionCode]     NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[ExceptionMemo]     NVARCHAR(100)   NULL        DEFAULT '',"
                    + "[SignatureType]     NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[CustomerSignature] NVARCHAR(50)    NULL        DEFAULT '',"
                    + "[TransportType]     NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[SignPhotoPath]     NVARCHAR(200)   NULL        DEFAULT '',"
                    + "[ToHawb]            NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[Weight]            NUMERIC(18,2)   NULL        DEFAULT 0,"
                    + "[IsCalTranferFee]   INT             NULL        DEFAULT 0,          --图像上传状态(0:无图像, 1:有图像, 2:图像已上传)\r\n"
                    + "[UploadStatus]      INT             NOT NULL    DEFAULT 0,          --上传状态\r\n"
                    + "[UploadTime]        NVARCHAR(20)    NULL        DEFAULT (''),       --上传时间\r\n"
                    + "[UploadErrMsg]      NVARCHAR(100)   NULL        DEFAULT '',"
                    + "[SealNum]           NVARCHAR(20)    NULL        DEFAULT '',         --车签号\r\n"
                    + "[FromToStationBak]  NVARCHAR(20)    NULL        DEFAULT '',          --备用站点\r\n"
                    + "[ClientId]          NVARCHAR(20)    NULL        DEFAULT ''          --客户ID\r\n"
                    + ")");
            // 填充数据
            db.execSQL("insert into tmsScanData select *,'' from _tmsScanData");
            // 删除临时表
            db.execSQL("drop table _tmsScanData");

            db.setTransactionSuccessful();
        } catch (SQLException ex) {
//            UrovoApplication.loger.error("DATABASE修改扫描表", ex);
        } finally {
            db.endTransaction();
        }

    }

    private boolean doCreateTable(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            /* 扫描数据表 */
            db.execSQL("CREATE TABLE tmsScanData"
                    + "("
                    + "[Id]                INTEGER         NOT NULL    PRIMARY KEY,"
                    + "[ScanBatch]         NVARCHAR(20)    NOT NULL    DEFAULT '',"
                    + "[FromToStation]     NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[BusinessMan]       NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[ScanType]          INT             NOT NULL    DEFAULT 10,"
                    + "[ScanHawb]          NVARCHAR(20)    NOT NULL    DEFAULT '',"
                    + "[ScanCarcode]       NVARCHAR(20)    NOT NULL    DEFAULT '',        --车牌号码\r\n"
                    + "[BagNumber]         NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[ScanUser]          NVARCHAR(20)    NOT NULL    DEFAULT '',"
                    + "[ScanTime]          Datetime        NOT NULL    DEFAULT (GETDATE()),"
                    + "[OperationTime]     Datetime        NULL        DEFAULT (GETDATE()),"
                    + "[ShiftTimes]        NVARCHAR(10)    NULL        DEFAULT '',"
                    + "[ScanStation]       NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[ExceptionCode]     NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[ExceptionMemo]     NVARCHAR(100)   NULL        DEFAULT '',"
                    + "[SignatureType]     NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[CustomerSignature] NVARCHAR(50)    NULL        DEFAULT '',"
                    + "[TransportType]     NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[SignPhotoPath]     NVARCHAR(200)   NULL        DEFAULT '',"
                    + "[ToHawb]            NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[Weight]            NUMERIC(18,2)   NULL        DEFAULT 0,"
                    + "[IsCalTranferFee]   INT             NULL        DEFAULT 0,          --图像上传状态(0:无图像, 1:有图像, 2:图像已上传)\r\n"
                    + "[UploadStatus]      INT             NOT NULL    DEFAULT 0,          --上传状态(-1:上传失败，0：未上传，1：上传成功，2：用户手工删除后，上传备份接口成功G035)\r\n"
                    + "[UploadTime]        NVARCHAR(20)    NULL        DEFAULT (''),       --上传时间\r\n"
                    + "[UploadErrMsg]      NVARCHAR(100)   NULL        DEFAULT '',"
                    + "[SealNum]           NVARCHAR(20)    NULL        DEFAULT '',         --车签号\r\n"
                    + "[FromToStationBak]  NVARCHAR(20)    NULL        DEFAULT ''          --备用站点\r\n"
                    + ")");
            /* 问题原因表 */
            db.execSQL("CREATE TABLE tmsException"
                    + "("
                    + "[ID]                INTEGER          NOT NULL    PRIMARY KEY,"
                    + "[ExceptionType]     NVARCHAR(20)    NOT NULL    DEFAULT '',"
                    + "[ExceptionCode]     NVARCHAR(20)    NOT NULL    DEFAULT '',"
                    + "[ExceptionDesc]     NVARCHAR(100)   NULL        DEFAULT '',"
                    + "[LastModifyUser]    NVARCHAR(20)    NULL        DEFAULT '', "
                    + "[LastModifyDate]     NVARCHAR(100)          NULL        DEFAULT ''"
                    + ")");
            /* 网点表 */
            db.execSQL("CREATE TABLE tmsStation"
                    + "("
                    + "[ID]                INTEGER          NOT NULL    PRIMARY KEY,"
                    + "[Province]          NVARCHAR(30)    NULL        DEFAULT '',"
                    + "[City]              NVARCHAR(30)    NULL        DEFAULT '',"
                    + "[StationCode]       NVARCHAR(20)    NOT NULL    DEFAULT '',"
                    + "[StationName]       NVARCHAR(100)   NULL        DEFAULT '',"
                    + "[LastModifyUser]    NVARCHAR(100)    NULL        DEFAULT '',"
                    + "[LastModifyDate]    NVARCHAR(100)          NULL        DEFAULT ''"
                    + ")");
            /* 员工表 */
            db.execSQL("CREATE TABLE tmsUserProfile"
                    + "("
                    + "[ID]                INTEGER          NOT NULL    PRIMARY KEY,"
                    + "[UserId]            NVARCHAR(20)    NOT NULL    DEFAULT '',"
                    + "[UserName]          NVARCHAR(40)    NULL        DEFAULT '',"
                    + "[Tel]               NVARCHAR(30)    NULL        DEFAULT '',"
                    + "[Password]          NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[StationCode]       NVARCHAR(20)    NULL        DEFAULT '',"
                    + "[StationName]       NVARCHAR(50)    NULL        DEFAULT '',"
                    + "[UserType]          INT             NULL        DEFAULT 3,"
                    + "[AdminFlag]         BIT             NULL        DEFAULT 'FALSE',"
                    + "[LastModifyUser]    NVARCHAR(20)    NULL        DEFAULT ''," // 密码密文
                    + "[LastModifyDate]     NVARCHAR(100)          NULL        DEFAULT ''"
                    + ")");
            /* 派件任务表 */
            db.execSQL("CREATE TABLE tmsDispatchTask"
                    + "("
                    + "[ID]            INTEGER          NOT NULL    PRIMARY KEY,"
                    + "[Hawb]          NVARCHAR(30)    NULL        DEFAULT '',     --运单号码\r\n"
                    + "[TaskStatus]    INT             NULL        DEFAULT 0       --任务状态(0:初始, 1:签收, 2:异常签收)\r\n"
                    + ")");
			/* 班次表 */
            db.execSQL("CREATE TABLE tmsShiftTime"
                    + "("
                    + "[ID]                INTEGER          NOT NULL    PRIMARY KEY,"
                    + "[Code]              NVARCHAR(30)    NULL        DEFAULT '',     --班次类型编号\r\n"
                    + "[Name]              NVARCHAR(30)    NULL        DEFAULT '',     --班次类型名称\r\n"
                    + "[LastModifyDate]     NVARCHAR(100)          NULL        DEFAULT ''"
                    + ")");
			/* 同行业信息表 */
            db.execSQL("CREATE TABLE tmsTransitCompany"
                    + "("
                    + "[ID]                INTEGER          NOT NULL    PRIMARY KEY,"
                    + "[Code]              NVARCHAR(30)    NULL        DEFAULT '',     --公司编号\r\n"
                    + "[Name]              NVARCHAR(30)    NULL        DEFAULT ''      --公司名称\r\n"
                    + ")");
			/* 车辆信息表 */
            db.execSQL("CREATE TABLE tmsTruckInfo"
                    + "("
                    + "[ID]            INTEGER          NOT NULL    PRIMARY KEY,"
                    + "[Code]          NVARCHAR(30)    NULL        DEFAULT '',             --公司编号\r\n"
                    + "[Name]          NVARCHAR(30)    NULL        DEFAULT '',             --公司名称\r\n"
                    + "[ModifyTime]    DATETIME        NULL        DEFAULT (GETDATE()) "
                    + ")");
			/* 撤销原因数据表 */
            db.execSQL("CREATE TABLE tmsOrderCancelReasonTable"
                    + "("
                    + "[ID]            INTEGER          NOT NULL    PRIMARY KEY,"
                    + "[Reason]        NVARCHAR(50)    NULL        DEFAULT ''                      -- 撤销原因\r\n"
                    + ")");
			/* 订单表 */
            db.execSQL("CREATE TABLE tmsOrderTable"
                    + "("
                    + "[ID]            INTEGER          NOT NULL    PRIMARY KEY,"
                    + "[OrderNo]       NVARCHAR(50)    NULL        DEFAULT '',                     -- 订单号\r\n"
                    + "[OrderUserID]   NVARCHAR(50)    NULL        DEFAULT '',                     -- 所属用户\r\n"
                    + "[OrderType]     NVARCHAR(50)    NULL        DEFAULT '',                     -- 类型\r\n"
                    + "[ConsAddress]   NVARCHAR(200)   NULL        DEFAULT '',                     -- 收件地址\r\n"
                    + "[OrderTime]     DATETIME		NULL        DEFAULT(GETDATE()),              -- 订单时间\r\n"
                    + "[ReadFlag]      INT             NULL        DEFAULT 0,                      -- 阅读状态\r\n"
                    + "[AcceptFlag]    INT             NULL        DEFAULT 0,                      -- 接单状态\r\n"
                    + "[CancelFlag]    INT             NULL        DEFAULT 0,                      -- 撤销状态\r\n"
                    + "[CancelReason]  NVARCHAR(50)    NULL        DEFAULT '',                     -- 撤销原因\r\n"
                    + "[ProcessedFlag] INT             NULL        DEFAULT 0                       -- 处理状态\r\n"
                    + ")");
			/* 订单操作流水表 */
            db.execSQL("CREATE TABLE tmsOrderProcessTable"
                    + "("
                    + "[ID]            INTEGER          NOT NULL    PRIMARY KEY,"
                    + "[OrderNo]       NVARCHAR(50)    NULL        DEFAULT '',                     -- 订单号\r\n"
                    + "[OperType]      INT             NULL        DEFAULT 0,                      -- 操作类型\r\n"
                    + "[CancelDesc]    NVARCHAR(50)    NULL        DEFAULT '',                     -- 撤销原因\r\n"
                    + "[OperUserID]    NVARCHAR(50)    NULL        DEFAULT '',                     -- 操作人员\r\n"
                    + "[OperTime]      DateTime        NULL        DEFAULT (GETDATE()),            -- 操作时间\r\n"
                    + "[UploadStatus]  INT             NULL        DEFAULT 0,                      -- 上传状态\r\n"
                    + "[UploadTime]    NVARCHAR(20)    NULL        DEFAULT ''                      -- 上传时间\r\n"
                    + ")");
			/* 订单扫描操作表 */
            db.execSQL("CREATE TABLE tmsOrderBillUploadTable"
                    + "("
                    + "[ID]            INTEGER          NOT NULL    PRIMARY KEY,"
                    + "[OrderNo]       NVARCHAR(50)    NULL        DEFAULT '',                     -- 订单编号\r\n"
                    + "[BillCodes]     NVARCHAR(200)   NULL        DEFAULT '',                     -- 单号条码\r\n"
                    + "[OperUserID]    NVARCHAR(50)    NULL        DEFAULT '',                     -- 员工编号\r\n"
                    + "[ProcessTime]   DateTime        NULL        DEFAULT (GETDATE()),            -- 处理时间\r\n"
                    + "[UploadStatus]  INT             NULL        DEFAULT 0,                      -- 上传状态\r\n"
                    + "[UploadTime]    NVARCHAR(20)    NULL        DEFAULT ''                      -- 上传时间\r\n"
                    + ")");

			/* G024订单表 Lm */
            // <a>中通订单号</a>
            // <b>合作商ID</b>
            // <c>合作商订单号 </c>
            // <d>寄件人省</d>
            // <e>寄件人市</e>
            // <f>寄件人区</f>
            // <g>寄件人详细地址</g>
            // <h>寄件人姓名</h>
            // <i>寄件人手机</i>
            // <j>寄件人座机</j>
            db.execSQL("CREATE TABLE tmsG024OrderTable"
                    + "("
                    + "[ID]            INTEGER          NOT NULL    PRIMARY KEY,"
                    + "[OrderNo]       NVARCHAR(50)    NULL        DEFAULT '',                     -- 订单号\r\n"
                    + "[OrderUserID]   NVARCHAR(50)    NULL        DEFAULT '',                     -- 合作商id\r\n"
                    + "[OrderType]     NVARCHAR(50)    NULL        DEFAULT '',                     -- 合作商订单号\r\n"
                    + "[province]   NVARCHAR(50)    NULL        DEFAULT '',                     -- 省\r\n"
                    + "[city]     NVARCHAR(50)    NULL        DEFAULT '',                     -- 市\r\n"
                    + "[area]   NVARCHAR(50)    NULL        DEFAULT '',                     -- 区\r\n"
                    + "[ConsAddress]   NVARCHAR(200)   NULL        DEFAULT '',                     -- 收件地址\r\n"
                    + "[Name]   NVARCHAR(50)    NULL        DEFAULT '',                     -- 寄件人姓名\r\n"
                    + "[Phone]     NVARCHAR(50)    NULL        DEFAULT '',                     -- 寄件人手机\r\n"
                    + "[Landline]   NVARCHAR(50)    NULL        DEFAULT '',                     --寄件人座机\r\n"
                    + "[OrderTime]     DATETIME		NULL        DEFAULT(GETDATE()),                  -- 订单时间\r\n"
                    + "[ReadFlag]      INT             NULL        DEFAULT 0,                      -- 阅读状态\r\n"
                    + "[AcceptFlag]    INT             NULL        DEFAULT 0,                      -- 接单状态\r\n"
                    + "[ProcessedFlag] INT             NULL        DEFAULT 0                       -- 处理状态\r\n"
                    + ")");

            db.execSQL("CREATE TABLE tmsTransitCompanValues" + "("
                    + "[ID] INTEGER NOT NULL PRIMARY KEY,"
                    + "[CODE] NVARCHAR(20) NOT NULL,"
                    + "[NAME] NVARCHAR(20) NOT NULL,"
                    + "[SITENUMBER] NVARCHAR(20) NOT NULL,"
                    + "[SITECODE] NVARCHAR(20) NOT NULL,"
                    + "[TIME] NVARCHAR(20) NOT NULL)");

            db.setTransactionSuccessful();

        } catch (SQLException ex) {
            // log
            db.endTransaction();
            return false;
        } finally {
            db.endTransaction();

        }
        return true;
    }

    private boolean doCreateIndex(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL("CREATE INDEX idx_tmsStation_StationID on tmsStation(StationCode)");
            db.execSQL("CREATE INDEX idx_tmsStation_Province on tmsStation(Province)");
            db.execSQL("CREATE INDEX idx_tmsStation_City on tmsStation(City)");
            db.execSQL("CREATE INDEX idx_tmsScanData_ScanType on tmsScanData(ScanType)");
            db.execSQL("CREATE INDEX idx_tmsScanData_ScanHawb on tmsScanData(ScanHawb)");
            db.execSQL("CREATE INDEX idx_tmsScanData_UploadStatus on tmsScanData(UploadStatus)");
            db.execSQL("CREATE INDEX idx_tmsScanData_ImageUploadStatus on tmsScanData(IsCalTranferFee)");
            db.execSQL("CREATE INDEX idx_tmsScanData_BagNumber on tmsScanData(ScanType, BagNumber)");
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            // log
            db.endTransaction();
            return false;
        } finally {
            db.endTransaction();
        }
        return true;
    }

    // 为扫描表添加索引
    private void doCreateIndexScan(SQLiteDatabase db) {

        try {
            db.beginTransaction();
            db.execSQL("CREATE INDEX idx_tmsScanData_ScanType on tmsScanData(ScanType)");
            db.execSQL("CREATE INDEX idx_tmsScanData_ScanHawb on tmsScanData(ScanHawb)");
            db.execSQL("CREATE INDEX idx_tmsScanData_UploadStatus on tmsScanData(UploadStatus)");
            db.execSQL("CREATE INDEX idx_tmsScanData_ImageUploadStatus on tmsScanData(IsCalTranferFee)");
            db.execSQL("CREATE INDEX idx_tmsScanData_BagNumber on tmsScanData(ScanType, BagNumber)");
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            // log
        } finally {
            db.endTransaction();
        }
    }

    // 清空问题表：后台下载类：LoginDowanloadValuesAsyncTask，除了G003表
    private void clearMatterTable(SQLiteDatabase db) {

        try {
            db.beginTransaction();

            // G004
            db.execSQL("DELETE FROM tmsStation");
            // G008
            db.execSQL("DELETE FROM TmsException");
            // G009
            db.execSQL("DELETE FROM tmsShiftTime");
            // G010
            // ----不用管

            // G012
            db.execSQL("DELETE FROM tmsTransitCompany");
            // G014
            db.execSQL("DELETE FROM tmsTruckInfo");
            // G025
            db.execSQL("DELETE FROM tmsClient");
            // G026
            db.execSQL("DELETE FROM tmsTransitCompanValues");

            db.setTransactionSuccessful();

        } catch (SQLException ex) {
            // log

        } finally {
            db.endTransaction();
        }

    }
}
