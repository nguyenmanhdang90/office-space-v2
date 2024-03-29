package com.ourfancyteamname.officespace.controllers;

import com.ourfancyteamname.officespace.services.ProcessService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class ProcessControllerTest {

  @InjectMocks
  private ProcessController controller;

  @Mock
  private ProcessService processService;

  @Test
  public void getByClusterId() {
    controller.getByClusterId(null);
    Mockito.verify(processService, Mockito.times(1)).getGraph(Mockito.any());
  }

  @Test
  public void addNodeToCluster() {
    controller.addNodeToCluster(null);
    Mockito.verify(processService, Mockito.times(1)).addNodeToCluster(Mockito.any());
  }

  @Test
  public void removeNodeFromCluster() {
    controller.removeNodeFromCluster(null);
    Mockito.verify(processService, Mockito.times(1)).removeNodeFromCluster(Mockito.any());
  }

  @Test
  public void editClusterNode() {
    controller.editClusterNode(null);
    Mockito.verify(processService, Mockito.times(1)).editClusterNode(Mockito.any());
  }

  @Test
  public void addPath() {
    controller.addPath(null, null);
    Mockito.verify(processService, Mockito.times(1)).addSinglePath(Mockito.any(), Mockito.any());
  }

  @Test
  public void removePath() {
    controller.removePath(null);
    Mockito.verify(processService, Mockito.times(1)).removePath(Mockito.any());
  }

}
