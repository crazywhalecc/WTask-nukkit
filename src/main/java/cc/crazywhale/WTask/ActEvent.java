package cc.crazywhale.WTask;

public class ActEvent<T> {
    private T item;
    public ActEvent(T t){
        item = t;
    }
    public T get(){
        return item;
    }
    public void set(T t){
        item = t;
    }
}
