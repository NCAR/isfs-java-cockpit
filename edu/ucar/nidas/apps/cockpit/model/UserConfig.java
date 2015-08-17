package edu.ucar.nidas.apps.cockpit.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.ucar.nidas.apps.cockpit.model.config.CockpitConfig;

/**
 * It maintains the user preferred cockpit UI configuration
 *  1> get a copy of the data-descriptor as default
 *  2> add plot config for each plot
 *  3> save the configuration
 *  4> retrieve the configuration if needed
 *     
 * @author dongl
 *
 */


public class UserConfig extends XmlDom {

    String _fn;
    PersistenceProject _pp;
    public static String  cockpitConfigXml = "cockpitConfig.xml";

    public UserConfig(CockpitConfig p, String fn){
        _pp = new PersistenceProject( p);
        _fn = fn;
        writeProject();
    }

    public UserConfig(){}

     
    private void writeProject() {
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try
        {
            fos = new FileOutputStream(_fn);
            out = new ObjectOutputStream(fos);
            out.writeObject(_pp);
            out.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void writeProject(CockpitConfig p, String fn) {
        _pp = new PersistenceProject( p);
        _fn = fn;
        writeProject();
    }
    
    public CockpitConfig getProject( String fn) {
        FileInputStream fis = null;
        ObjectInputStream in = null;

        try
        {
            fis = new FileInputStream(fn);
            in = new ObjectInputStream(fis);
            _pp = (PersistenceProject)in.readObject();
            in.close();
            return _pp.getProject();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CockpitConfig getProject() {
       return getProject(_fn);
    }
    

    public class PersistenceProject implements Serializable {
        CockpitConfig _p;
        public PersistenceProject(CockpitConfig p) {
            _p = p;
            // TODO Auto-generated constructor stub
        }

        public CockpitConfig getProject() {
            return _p;
        }
    }

}
