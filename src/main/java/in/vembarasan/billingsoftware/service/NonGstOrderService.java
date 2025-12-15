package in.vembarasan.billingsoftware.service;


import in.vembarasan.billingsoftware.entity.NonGstOrderEntity;
import in.vembarasan.billingsoftware.io.OrderRequest;

public interface NonGstOrderService {

    NonGstOrderEntity createNonGstOrder(OrderRequest request);


}
