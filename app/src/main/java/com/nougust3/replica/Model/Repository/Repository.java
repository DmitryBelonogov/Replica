package com.nougust3.replica.Model.Repository;

public interface Repository<T> {

    void add(T item);
    void remove(T item);
    void update(T note);

}
