package org.pentaho.commons.metadata.mqleditor.editor.controllers;

import org.pentaho.commons.metadata.mqleditor.editor.models.Workspace;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.containers.XulTree;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;
import org.pentaho.ui.xul.stereotype.Bindable;

public class SelectedColumnController extends AbstractXulEventHandler {

  private XulTree columnTree;
  private Workspace workspace;
  private BindingFactory bf;
  
  @Bindable
  public void init(){
    columnTree = (XulTree) document.getElementById("selectedColumnTree");

    BindingConvertor<int[], Boolean> buttonConvertor = new BindingConvertor<int[], Boolean>(){

      @Override
      public Boolean sourceToTarget(int[] value) {
        return (value == null || value.length ==0);  
      }

      @Override
      public int[] targetToSource(Boolean value) {return null;}
        
    };
    
    bf.setBindingType(Binding.Type.ONE_WAY);
    bf.createBinding(columnTree,"selectedRows", "colUp", "disabled", buttonConvertor);
    bf.createBinding(columnTree,"selectedRows", "colDown", "disabled", buttonConvertor);
    bf.createBinding(columnTree,"selectedRows", "colRemove", "disabled", buttonConvertor);
  }
    
  public int getSelectedIndex() {
    int[] rows = this.columnTree.getSelectedRows();
    return (rows != null && rows.length == 0) ? -1 : rows[0];
  }

  @Bindable
  public void moveUp(){
    try{
      int prevIndex = getSelectedIndex();
      workspace.getSelections().moveChildUp(getSelectedIndex());
      columnTree.clearSelection();
      columnTree.setSelectedRows(new int[]{prevIndex -1});
    } catch(IllegalArgumentException e){
      //out of bounds
    }
  }

  @Bindable
  public void moveDown(){
    int prevIndex = getSelectedIndex();
    try{
      workspace.getSelections().moveChildDown(getSelectedIndex());
      columnTree.clearSelection();
      columnTree.setSelectedRows(new int[]{prevIndex+1});
    } catch(IllegalArgumentException e){
      //out of bounds
    }
  }
  
  @Bindable
  public void remove(){
    if(getSelectedIndex() < 0){
      return;
    }
    workspace.getSelections().remove(getSelectedIndex());
    columnTree.clearSelection();
  }

  public void setWorkspace(Workspace workspace){
    this.workspace = workspace;
  }

  public String getName() {
    return "selectedColumns";
  }
  
  public void setBindingFactory(BindingFactory bf){
    this.bf = bf;
  }
}