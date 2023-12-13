package io.camunda.getstarted;

import io.camunda.zeebe.client.ZeebeClient;

public class ZeebeClientFactory {

  public static ZeebeClient getZeebeClient() {
    return ZeebeClient.newCloudClientBuilder()
        .withClusterId("1935113a-58c6-4af6-8c65-2dcd1cfa00e3")
        .withClientId("4NediT1bCuU4xNzejNP46sEMNov5BLUs")
        .withClientSecret("y~t7tKonjez-2DIvrAlMI8mf_~utaW0KiV.eeG_fNmp-1DHrv~f5EQERkusyBdIE")
        .withRegion("bru-2")
        .build();
  }

}
