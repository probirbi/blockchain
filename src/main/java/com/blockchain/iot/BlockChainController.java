package com.blockchain.iot;

import com.blockchain.iot.model.Block;

import com.blockchain.iot.model.ParkingSpace;
import com.blockchain.iot.model.Sensor;
import com.blockchain.iot.model.SmartHome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class BlockChainController {

    List<Block> blockChain = new ArrayList<Block>();

    int prefix = 1;

    String prefixString = new String(new char[prefix]).replace('\0', '0');

    @GetMapping("/blockchain")
    public List<Block> getBlockchain() {

        for (int i = 0; i < blockChain.size(); i++) {
            String previousHash = i == 0 ? "0"
                    : blockChain.get(i - 1)
                    .getHash();
            boolean flag = blockChain.get(i)
                    .getHash()
                    .equals(blockChain.get(i)
                            .calculateBlockHash())
                    && previousHash.equals(blockChain.get(i)
                    .getPreviousHash())
                    && blockChain.get(i)
                    .getHash()
                    .substring(0, prefix)
                    .equals(prefixString);
            if (flag) {
                System.out.println("Blocks in the block chain is validated");
            }
        }
        return blockChain;
    }

    @PostMapping("/blockchain")
    public String saveBlockchain(@RequestBody Block block) {

        if (blockChain.size() == 0) {
            Block blockNew = new Block(block.getDescription(), block.getData(), "0", new Date().getTime(), block.getNode());
            blockNew.mineBlock(prefix);
            blockChain.add(blockNew);
        } else {
            Block blockNew = new Block(block.getDescription(), block.getData(), blockChain.get(blockChain.size() - 1).getHash(), new Date().getTime(), block.getNode());
            blockNew.mineBlock(prefix);
            blockChain.add(blockNew);
        }
        System.out.println("Block No: "+blockChain.size());
        return "success";
    }

    @PostMapping("/blockchainevaluatenode")
    public String saveBlockchain(@RequestParam int node) {
        for (int i = 0; i < blockChain.size(); i++) {
            if (node == blockChain.get(i).getNode()) {
                switch (node) {
                    case 1 :
                        evaluationLogicOne(blockChain.get(i));
                        break;
                    case 2 :
                        evaluationLogicTwo(blockChain.get(i));
                        break;
                    case 3 :
                        evaluationLogicThree(blockChain.get(i));
                        break;
                }
            }
        }
        return "Node " + node + " evaluated";

    }

    private void evaluationLogicOne(Block block) {

        Sensor sensor = (Sensor) block.getData();
        if (sensor.getTemperatureCelsius() >= 10 && sensor.getTemperatureCelsius() <= 20) {
            block.setTrustScore(1.0);
        } else {
            block.setTrustScore(0.5);
        }
    }

    private void evaluationLogicTwo(Block block) {
        SmartHome smartHome = (SmartHome) block.getData();
        if (smartHome.getSmokeDetectors() >= 2 && smartHome.getDoorLocks() <= 2) {
            block.setTrustScore(1.0);
        } else {
            block.setTrustScore(0.4);
        }
    }

    private void evaluationLogicThree(Block block) {
        ParkingSpace parkingSpace = (ParkingSpace) block.getData();
        if (parkingSpace.getParkedSpace() > 250 && parkingSpace.getFreeSpace() < 100) {
            block.setTrustScore(1.0);
        } else {
            block.setTrustScore(0.2);
        }
    }
}
