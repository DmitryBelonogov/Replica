package com.nougust3.replica.Model.Repository;

interface Repository<T> {

    T get(long id);
    void update(T item);
    void remove(T item);

}
