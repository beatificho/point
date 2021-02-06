package cafeteria;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import cafeteria.config.kafka.KafkaProcessor;

@Service
public class PolicyHandler{
	
	@Autowired
	private PointRepository pointRepository;
	
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentApproved_(@Payload PaymentApproved paymentApproved){

        if(paymentApproved.isMe()){
		System.out.println("##### listener  : " + paymentApproved.toJson());
        	List<Point> points = pointRepository.findByPhoneNumber(paymentApproved.getPhoneNumber());
        	int p = (int)(paymentApproved.getAmt() * 0.1);
        	if(points.size() == 0) {
        		Point point = new Point();
        		point.setPhoneNumber(paymentApproved.getPhoneNumber());
        		point.setPoint(p);
        		pointRepository.save(point);
        	} else {
	        	for(Point point : points) {
	        		point.setPoint(point.getPoint() + p);
	        		pointRepository.save(point);
	        	}
        	}
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCanceled_(@Payload PaymentCanceled paymentCanceled){

        if(paymentCanceled.isMe()){
		System.out.println("##### listener  : " + paymentCanceled.toJson());
        	List<Point> points = pointRepository.findByPhoneNumber(paymentCanceled.getPhoneNumber());
        	int p = (int)(paymentCanceled.getAmt() * 0.1);
        	
        	for(Point point : points) {
        		point.setPoint(point.getPoint() - p);
        		pointRepository.save(point);
        	}
        }
    }

}
