package com.example.uav.service.impl;

import com.example.uav.domain.dto.UavDTO;
import com.example.uav.service.AiAttributeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * AI 属性生成服务实现（基于规则策略），根据传入的等级或型号关键词匹配消费级/工业级/军用级三档等级，在对应范围内随机生成属性值。
 */
@Slf4j
@Service
public class AiAttributeServiceImpl implements AiAttributeService {

    private static final Random RANDOM = new Random();

    /**
     * 根据无人机型号和等级生成属性。
     * 如果 grade 非空则直接使用，否则通过 model 关键词自动匹配等级。
     *
     * @param model 无人机型号
     * @param grade 可选等级（CONSUMER / INDUSTRIAL / MILITARY），可为 null
     * @return 填充了属性字段的 UavDTO（id 为 null）
     */
    @Override
    public UavDTO generateAttributes(String model, String grade) {
        log.info("AI 属性生成，型号={}, 等级={}", model, grade);
        UavGrade resolvedGrade = resolveGrade(grade, model);
        return buildDto(model, resolvedGrade);
    }

    /**
     * 解析等级：优先使用前端传入的 grade，为空时通过 model 关键词兜底。
     *
     * @param grade 前端传入的等级，可能为 null 或空
     * @param model 型号描述
     * @return 无人机等级
     */
    private UavGrade resolveGrade(String grade, String model) {
        if (grade != null && !grade.isEmpty()) {
            try {
                return UavGrade.valueOf(grade.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("无效的 grade 值 '{}'，回退到关键词匹配", grade);
            }
        }
        return detectGrade(model);
    }

    /**
     * 根据型号关键词推断无人机等级。
     *
     * @param model 型号描述
     * @return 无人机等级
     */
    private UavGrade detectGrade(String model) {
        if (model == null) {
            return UavGrade.CONSUMER;
        }
        String lower = model.toLowerCase();
        if (lower.contains("军用") || lower.contains("military") || lower.contains("combat")) {
            return UavGrade.MILITARY;
        }
        if (lower.contains("工业") || lower.contains("industrial") || lower.contains("enterprise")) {
            return UavGrade.INDUSTRIAL;
        }
        return UavGrade.CONSUMER;
    }

    /**
     * 根据等级在合理范围内随机生成属性。
     *
     * @param model 型号描述
     * @param grade 无人机等级
     * @return UavDTO
     */
    private UavDTO buildDto(String model, UavGrade grade) {
        return UavDTO.builder()
                .model(model)
                .manufacturer(randomManufacturer())
                .maxPayload(randomDouble(grade.minPayload, grade.maxPayload))
                .maxAltitude(randomInt(grade.minAltitude, grade.maxAltitude))
                .maxFlightTime(randomInt(grade.minFlightTime, grade.maxFlightTime))
                .maxSpeed(randomDouble(grade.minSpeed, grade.maxSpeed))
                .wingspan(randomDouble(grade.minWingspan, grade.maxWingspan))
                .weight(randomDouble(grade.minWeight, grade.maxWeight))
                .status(1)
                .aiGenerated(1)
                .remark("AI 生成，等级：" + grade.label)
                .build();
    }

    private double randomDouble(double min, double max) {
        return Math.round((min + RANDOM.nextDouble() * (max - min)) * 10.0) / 10.0;
    }

    private int randomInt(int min, int max) {
        return min + RANDOM.nextInt(max - min + 1);
    }

    private static final String[] MANUFACTURERS = {
        "大疆创新（DJI）", "极飞科技（XAG）", "亿航智能（EHang）",
        "纵横股份（JOUAV）", "中航无人机", "航天彩虹（CH UAV）",
        "科比特航空（MMC）", "华科尔（Walkera）", "普宙飞行器（GDU）",
        "零度智控（ZEROTECH）", "派诺特（Parrot）", "斯凯迪奥（Skydio）",
        "SenseFly", "Yuneec", "Autel Robotics"
    };

    private String randomManufacturer() {
        return MANUFACTURERS[RANDOM.nextInt(MANUFACTURERS.length)];
    }

    /**
     * 无人机等级及对应属性范围枚举。
     */
    private enum UavGrade {
        CONSUMER("消费级",  0.5,  5.0,  100, 3000,   20, 60,  8,  25,  30, 120,   0.5, 3.0),
        INDUSTRIAL("工业级", 5.0, 30.0, 2000, 6000,   40, 120, 15, 50,  80, 250,   3.0, 15.0),
        MILITARY("军用级", 30.0, 200.0, 5000, 20000, 120, 480, 50, 300, 150, 600, 15.0, 80.0);

        final String label;
        final double minPayload, maxPayload;
        final int minAltitude, maxAltitude;
        final int minFlightTime, maxFlightTime;
        final double minSpeed, maxSpeed;
        final double minWingspan, maxWingspan;
        final double minWeight, maxWeight;

        UavGrade(String label,
                 double minPayload, double maxPayload,
                 int minAltitude, int maxAltitude,
                 int minFlightTime, int maxFlightTime,
                 double minSpeed, double maxSpeed,
                 double minWingspan, double maxWingspan,
                 double minWeight, double maxWeight) {
            this.label = label;
            this.minPayload = minPayload; this.maxPayload = maxPayload;
            this.minAltitude = minAltitude; this.maxAltitude = maxAltitude;
            this.minFlightTime = minFlightTime; this.maxFlightTime = maxFlightTime;
            this.minSpeed = minSpeed; this.maxSpeed = maxSpeed;
            this.minWingspan = minWingspan; this.maxWingspan = maxWingspan;
            this.minWeight = minWeight; this.maxWeight = maxWeight;
        }
    }
}
