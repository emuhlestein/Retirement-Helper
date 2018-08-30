package com.intelliviz.db.entity;

import com.intelliviz.data.IncomeData;
import com.intelliviz.data.IncomeDataAccessor;
import com.intelliviz.data.IncomeSourceType;

import java.util.List;

public abstract class AbstractIncomeSource implements IncomeSourceType {
    private long mId;
    private int mType;
    private String mName;
    private int mOwner;

    public AbstractIncomeSource(long id, int type, String name, int owner) {
        mId = id;
        mType = type;
        mName = name;
        mOwner = owner;
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public int getType() {
        return mType;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setId(long id) {
        mId = id;
    }

    @Override
    public void setType(int type) {
        mType = type;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public int getOwner() {
        return mOwner;
    }

    @Override
    public void setOwner(int owner) {
        mOwner = owner;
    }

    public abstract List<IncomeData> getIncomeData();

    public abstract IncomeDataAccessor getIncomeDataAccessor();
}
