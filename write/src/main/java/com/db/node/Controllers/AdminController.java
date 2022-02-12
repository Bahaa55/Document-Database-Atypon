package com.db.node.Controllers;

import com.db.node.Services.AdminService;

public class AdminController {
    private AdminService adminService = AdminService.getInstance();

    public void addNode(String port){
        adminService.addNodeToCluster(port);
    }

    public void scale(){
        adminService.scaleHorizontally();
    }

}
