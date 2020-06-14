package com.agh.edu.pankracy.data.plants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.agh.edu.pankracy.utils.DateUtils;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends OrmLiteSqliteOpenHelper {


    public static final String DB_NAME = "plant_manager.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {

            TableUtils.createTable(connectionSource, Plant.class);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {

            TableUtils.dropTable(connectionSource, Plant.class, true);
            TableUtils.createTable(connectionSource, Plant.class);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Plant> getAll() throws SQLException {
        Dao<Plant, ?> dao = getDao(Plant.class);
        return dao.queryForAll();
    }

    public Plant getById(Object aId) throws SQLException {
        Dao<Plant, Object> dao = getDao(Plant.class);
        return dao.queryForId(aId);
    }

    public Dao.CreateOrUpdateStatus createOrUpdate(Plant obj) throws SQLException {
        Dao<Plant, ?> dao = (Dao<Plant, ?>) getDao(Plant.class);
        return dao.createOrUpdate(obj);
    }

    public int deleteById(Long id) throws SQLException {
        Dao<Plant, Object> dao = getDao(Plant.class);
        return dao.deleteById(id);
    }

    public List<Plant> getPlantsForDate(java.util.Date date) throws SQLException{
        List<Plant> allPlants = getAll();
        List<Plant> result = new ArrayList<>();
        for (Plant plant : allPlants){
            try {
                long days = DateUtils.getNumberOfDaysBetweenGivenDateAndNextWatering(date, DateUtils.sdf.parse(plant.getLastWatering()), plant.getWatering());
                if (days < 0)
                    result.add(plant);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
