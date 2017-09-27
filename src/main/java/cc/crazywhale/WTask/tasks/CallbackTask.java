package cc.crazywhale.WTask.tasks;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class CallbackTask extends Task {

    protected String callable;
    protected String function;
    protected ArrayList<Object> args;
    protected Method m;
    protected Class<?> obj;

    public CallbackTask(String callable, String function, ArrayList<Object> args){
        this.callable = callable;
        this.function = function;
        this.args = args;
        try{initializeClass(); } catch(Exception e){
            String reason = "";
            if(e instanceof ClassNotFoundException){
                reason = "Unknown Class Name!";
            }
            else if(e instanceof IllegalAccessException){
                reason = "IllegalAccess!";
            }
            else if(e instanceof NoSuchMethodException){
                reason = "No such method!";
            }
            System.out.println("Unable to initialize Class! Reason: " + reason);
        }
    }
    public CallbackTask(Class<? extends PluginBase> callable, String function, ArrayList<Object> args){
        this.callable = callable.getName();
        this.function = function;
        this.args = args;
        try{initializeClass(); } catch(Exception e){System.exit(0);}
    }

    public void initializeClass() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException{
        obj = Class.forName(this.callable);
        Method m = obj.getDeclaredMethod(function, String.class);
        this.m = m;
    }

    public void onRun(int currentTick){
        try{
            m.invoke(obj,args);
        }
        catch(Exception e){
            System.exit(0);
        }
    }
}
