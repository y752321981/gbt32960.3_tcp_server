package com.camellya.gbt32960_3_tcp_server.controller;

import com.camellya.gbt32960_3_tcp_server.model.entity.NodeInfo;
import com.camellya.gbt32960_3_tcp_server.service.INodeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/node")
public class NodeController {

    @Resource
    private INodeService nodeService;

    @GetMapping("/list")
    public List<NodeInfo> getNodeList() {
        return nodeService.getNodeList();
    }
}
