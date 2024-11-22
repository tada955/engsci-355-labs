package labs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import hccm.activities.ProcessActivity;
import hccm.controlunits.ControlUnit;
import hccm.entities.ActiveEntity;
import hccm.events.LogicEvent;

public class RC2ControlUnit extends ControlUnit {
  
  public void OnStartWaitForCheckIn(List<ActiveEntity> ents, double simTime) {
        
    ArrayList<ActiveEntity> idleReceps = this.getEntitiesInActivity("ReceptionistEntity", "WaitForTaskReceptionist", simTime);
    ActivityStartCompare actStartComp = this.new ActivityStartCompare();        
    
    if (idleReceps.size() > 0) {
      Collections.sort(idleReceps, actStartComp);
      
      ActiveEntity patient = ents.get(0);
      ActiveEntity receptionist = idleReceps.get(0);
      
      transitionTo("CheckIn", patient, receptionist);
    }
  }
  
  public void OnStartWaitForScan(List<ActiveEntity> ents, double simTime) {
        
    ArrayList<ActiveEntity> idleCTs = this.getEntitiesInActivity("CTMachineEntity", "WaitForTaskCTMachine", simTime);
    ActivityStartCompare actStartComp = this.new ActivityStartCompare();        
    
    if (idleCTs.size() > 0) {
      Collections.sort(idleCTs, actStartComp);
      
      ActiveEntity patient = ents.get(0);
      ActiveEntity ct = idleCTs.get(0);

      transitionTo("Scan", patient, ct);
    }
  }
    
  public void OnStartWaitForTaskReceptionist(List<ActiveEntity> ents, double simTime) {
        
    ArrayList<ActiveEntity> waitPats = this.getEntitiesInActivity("PatientEntity", "WaitForCheckIn", simTime);
    ActivityStartCompare actStartComp = this.new ActivityStartCompare();        
    
    if (waitPats.size() > 0) {
      Collections.sort(waitPats, actStartComp);
      
      ActiveEntity patient = waitPats.get(0);
      ActiveEntity receptionist = ents.get(0);

      transitionTo("CheckIn", patient, receptionist);
    }
  }

  public void OnStartWaitForTaskCTMachine(List<ActiveEntity> ents, double simTime) {
    
    ArrayList<ActiveEntity> waitPats = this.getEntitiesInActivity("PatientEntity", "WaitForScan", simTime);
    AttributeCompare priorityComp = new AttributeCompare("priority");
    ActivityStartCompare actStartComp = this.new ActivityStartCompare();        
    ActiveEntity ct = ents.get(0);
    int reqMaintenance = (int) getNumAttribute(ct, "NeedMaintenance", simTime, -1);
    
    if (reqMaintenance == 1) {
      // A //
    }
      
    else if (waitPats.size() > 0) {
      Collections.sort(waitPats, priorityComp);
      
      int highestPriority = // B //
      
      ArrayList<ActiveEntity> priorityPatients = new ArrayList<ActiveEntity>();
      for (ActiveEntity wP : waitPats) {
        int patPri = // C //
        if (patPri == highestPriority) {
          priorityPatients.add(wP);
        }
      }
      
      Collections.sort(priorityPatients, actStartComp);
      ActiveEntity patient = priorityPatients.get(0);
                
      transitionTo("Scan", patient, ct);
    }
  }
    
  public void OnCTArrival(List<ActiveEntity> ents, double simTime) {
        
    double maintenanceTimeGap = 8 * 60 * 60;
    LogicEvent le = (LogicEvent) getSubmodelEntity("RequireMaintenance");
    
    le.scheduleEvent(ents, maintenanceTimeGap);
  }
    
  public void OnRequireMaintenance(List<ActiveEntity> ents, double simTime) {
        
    double maintenanceTimeGap = 8 * 60 * 60;
    LogicEvent le = (LogicEvent) getSubmodelEntity("RequireMaintenance");
    
    le.scheduleEvent(ents, simTime + maintenanceTimeGap);
    
    ActiveEntity ct = ents.get(0);
    if (ct.getCurrentActivity(simTime).equals("WaitForTaskCTMachine")) {
      transitionTo("Maintenance", ct);
    }      
  } 
        
}
