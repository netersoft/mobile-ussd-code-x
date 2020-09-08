package com.neteru.mobileussdcodex.classes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.neteru.mobileussdcodex.classes.models.Ussd;

import java.util.List;

@SuppressWarnings("unused")
public class DatabaseManager extends OrmLiteSqliteOpenHelper{

    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "Mucx.db";
    private final static String TAG = "DB_MANAGER";
    private Dao<Ussd, Integer> dao;

    public DatabaseManager(Context ctx){
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Ussd.class);
        }catch (Exception e){
            Log.e(TAG, "Erreur lors de la création de la Table - "+e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {

            TableUtils.dropTable(connectionSource, Ussd.class, true);
            TableUtils.createTable(connectionSource, Ussd.class);

        }catch (Exception e){
            Log.e(TAG, "Erreur lors de la mise à jour de la Table - "+e);
        }
    }

    public long db_insertValue(Ussd ussd){
        try {

            getDao(Ussd.class).create(ussd);
            return ussd.getId();

        }catch (Exception e){

            Log.e(TAG, "Erreur lors de l'insertion de l'objet dans la Table - "+e);
            return -1;
        }
    }

    public List<Ussd> db_readAll(){
        try {

            return getDao(Ussd.class).queryForAll();

        }catch (Exception e){

            Log.e(TAG, "Erreur lors de la lecture de toute la Table - "+e);
            return null;
        }
    }

    public List<Ussd> db_readByFragment(String fragment){
        try {

            dao = getDao(Ussd.class);
            QueryBuilder<Ussd, Integer> qb = dao.queryBuilder();

            qb.where().eq("fragment", fragment);
            qb.orderBy("isFavorite", false);
            qb.orderBy("isNative", false);

            return qb.query();

        }catch (Exception e){

            Log.e(TAG, "Erreur lors de la lecture par fragment de la Table - "+e);
            return null;
        }
    }

    public List<Ussd> db_readByDescription(String description){
        try {

            return getDao(Ussd.class).queryForEq("description", description);

        }catch (Exception e){

            Log.e(TAG, "Erreur lors de la lecture par description de la Table - "+e);
            return null;
        }
    }

    public List<Ussd> db_readByCode(String code){
        try {

            return getDao(Ussd.class).queryForEq("code", code);

        }catch (Exception e){

            Log.e(TAG, "Erreur lors de la lecture par code de la Table - "+e);
            return null;
        }
    }

    public List<Ussd> db_readByIsNative(boolean isNative){
        try {

            return getDao(Ussd.class).queryForEq("isNative", isNative);

        }catch (Exception e){

            Log.e(TAG, "Erreur lors de la lecture si natif de la Table - "+e);
            return null;
        }
    }

    public List<Ussd> db_readByIsFavorite(boolean isFavorite){
        try {

            return getDao(Ussd.class).queryForEq("isFavorite", isFavorite);

        }catch (Exception e){

            Log.e(TAG, "Erreur lors de la lecture si favoris de la Table - "+e);
            return null;
        }
    }

    public long db_updateValue(Ussd ussd){
        try {

            Dao<Ussd, Integer> dao = getDao(Ussd.class);
            UpdateBuilder<Ussd, Integer> ub = dao.updateBuilder();

            ub.updateColumnValue("isFavorite",ussd.getIsFavorite());
            ub.where().eq("description",ussd.getDescription())
                      .and()
                      .eq("code",ussd.getCode())
                      .and()
                      .eq("fragment",ussd.getFragment());
            ub.update();

            return ussd.getId();

        }catch (Exception e){

            Log.e(TAG, "Erreur lors de la mise à jour dans la Table - "+e);
            return -1;
        }
    }

    public long db_removeValue(Ussd ussd){
        try {

            Dao<Ussd, Integer> dao = getDao(Ussd.class);
            DeleteBuilder<Ussd, Integer> ub = dao.deleteBuilder();

            ub.where().eq("description",ussd.getDescription())
                      .and()
                      .eq("code",ussd.getCode())
                      .and()
                      .eq("fragment",ussd.getFragment());
            ub.delete();

            return ussd.getId();

        }catch (Exception e){

            Log.e(TAG, "Erreur lors de la suppression dans la Table - "+e);
            return -1;
        }
    }

    public void db_removeValueByIsNative(){
        try {

            Dao<Ussd, Integer> dao = getDao(Ussd.class);
            DeleteBuilder<Ussd, Integer> ub = dao.deleteBuilder();

            ub.where().eq("isNative", true);
            ub.delete();

        }catch (Exception e){
            Log.e(TAG, "Erreur lors de la suppression dans la Table - "+e);
        }
    }

    public List<Ussd> db_searchByDescriptionAndCode(String q){
        try {

            dao = getDao(Ussd.class);
            QueryBuilder<Ussd, Integer> qb = dao.queryBuilder();

            qb.where().like("description", "%"+q+"%").or().like("code", "%"+q+"%");
            PreparedQuery<Ussd> pq = qb.prepare();

            return dao.query(pq);

        }catch (Exception e){

            Log.e(TAG, "Erreur lors de la lecture par description de la Table - "+e);
            return null;
        }
    }
}
