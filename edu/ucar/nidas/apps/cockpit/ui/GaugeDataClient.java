package edu.ucar.nidas.apps.cockpit.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.trolltech.qt.gui.QApplication;

import edu.ucar.nidas.apps.cockpit.model.DataClient;
import edu.ucar.nidas.core.FloatSample;
import edu.ucar.nidas.model.Var;

/**
 * This class acts as a gauge's data-client to receive the data,
 * and implements a QT thread to invoke the cockpit-UI for display  
 * @author dongl
 *
 */
public class GaugeDataClient implements DataClient,Runnable
{
    private Gauge _g=null;      //primary gauge
    private Var _var;           //Var corresponding to the primary gauge
    private GaugePage _pw=null; //primary gauge's parent-page    

    /**
     * List of other plots in addition to the primary one
     * @author dongl
     */
    List<Gauge> _gs = new ArrayList<Gauge>();

    class SampOffset {
        FloatSample _samp;
        int _offset;
        public SampOffset (FloatSample samp, int offset) {
            _samp=samp;
            _offset=offset;
        }

        public FloatSample getSamp(){
            return _samp;
        }

        public int getOffset() {
            return _offset;
        }
    }
    /**
     * sample list, at this time, the sample data[2]: data[0]=min, data[1]=max;  Therefore, offset=0 (always)
     */
    private LinkedList<SampOffset> _samps= new LinkedList<SampOffset>(); 


    public GaugeDataClient( GaugePage pw, Var var, Gauge g ) {
        _pw=pw;
        _var=var;
        _g = g;

    }

    /**
     * public interface to allow the cockpit program to add more plots' listeners
     * @param g
     */
    public void addGauge(Gauge g) {
        _gs.add(g);
    }
    /**
     * receive a variable-stat-data that contains min-max
     */
    public void receive(FloatSample samp, int offset)
    {
        synchronized (this) {
            if (_samps.size() < 100000) _samps.add(new SampOffset(samp,offset));
            if (_samps.size() == 1) QApplication.invokeLater(this);
        }
    }

    /**
     * This implements a Runable to keep the thread alive
     */
    public void run() 
    {
        if (_g==null){ 
            if (_pw==null || _var==null) return;
            synchronized (this) {
                _g=_pw.createGauge(_var);
            }
        }

        if (_g==null) return;  
        SampOffset smp;
        for (;;) {
            synchronized(this) {
                if (_samps.size() == 0) return;
                smp = _samps.remove();

            
                _g.receive(smp.getSamp(), smp.getOffset());
                distributeDataToPlots(smp.getSamp(), smp.getOffset());
            }
        }
    }


    private void distributeDataToPlots(FloatSample samp, int offset) {
        for (int i=0; i< _gs.size(); i++){
            Gauge g= _gs.get(i);
            if (g==null) continue;
            g.receive(samp, offset);
        }
    }

    public void removeClient(DataClient dc) {}
    public void addClient(DataClient dc) {}
}
