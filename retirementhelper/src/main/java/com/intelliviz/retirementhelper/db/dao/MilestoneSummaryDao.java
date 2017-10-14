package com.intelliviz.retirementhelper.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.intelliviz.retirementhelper.db.entity.MilestoneSummaryEntity;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by edm on 10/2/2017.
 */
@Dao
public interface MilestoneSummaryDao {
    @Insert(onConflict = REPLACE)
    long insert(MilestoneSummaryEntity govPension);

    @Query("SELECT * FROM " + MilestoneSummaryEntity.TABLE_NAME + " WHERE id = :id")
    MilestoneSummaryEntity get(long id);

    @Query("SELECT * FROM " + MilestoneSummaryEntity.TABLE_NAME)
    List<MilestoneSummaryEntity> getAll();

    @Update(onConflict = REPLACE)
    void update(MilestoneSummaryEntity govPension);

    @Delete
    void delete(MilestoneSummaryEntity govPension);
}