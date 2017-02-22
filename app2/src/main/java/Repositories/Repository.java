package Repositories;

import java.util.List;

public interface Repository<T> {

    void add(T note);
    void add(Iterable<T> items);
    void remove(T note);
    void remove(SqlSpecification specification);
    void update(T note);

    List<T> query(SqlSpecification specification);

}
