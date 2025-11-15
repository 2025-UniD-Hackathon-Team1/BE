package practice.deploy.coffee.util;

import java.util.HashMap;
import java.util.Map;

public class CaffeineModel {

    // 1. 반감기(t½) 추정
    public static double estimateTHalf(int age, String metabolizer) {
        double baseHalfLife = 5.0; // 기본값

        // 대사 능력에 따라 반감기에 곱할 보정치
        Map<String, Double> metabolizerFactors = new HashMap<>();
        metabolizerFactors.put("slow", 1.6);
        metabolizerFactors.put("normal", 1.0);
        metabolizerFactors.put("fast", 0.7);

        baseHalfLife *= metabolizerFactors.getOrDefault(metabolizer, 1.0);

        // 청년 중년 장년 노년에 따른 분류 및 반감기 세밀조정
        double ageFactor;
        if (age < 18) {
            ageFactor = 1.2;
        } else if (age < 40) {
            ageFactor = 0.95;
        } else if (age < 65) {
            ageFactor = 1.0 + (age - 40) * 0.008;
        } else {
            ageFactor = 1.2 + (age - 65) * 0.015;
        }

        return baseHalfLife * ageFactor;
    }

    // 2. 내성 변화모델 (평소 섭취량 따른 카페인 효과 차이 부여)
    public static double toleranceFactor(int weekNumber, int daysSinceLast) {
        double acuteTolerance = 1.0;

        if (weekNumber >= 7) {
            acuteTolerance = 0.5;
        } else if (weekNumber >= 4) {
            acuteTolerance = 0.7;
        } else if (weekNumber >= 2) {
            acuteTolerance = 0.85;
        }

        if (daysSinceLast > 3) {
            acuteTolerance = Math.min(1.0, acuteTolerance + 0.15);
        }

        return acuteTolerance;
    }

    // 연령에 따른 카페인 민감도 변화 계산(어릴수록 민감)
    public static double receptorSensitivity(int age) {
        if (age < 25) {
            return 1.0;
        } else if (age < 50) {
            // 25~49세: 매년 0.4%씩 민감도 감소
            return 1.0 - (age - 25) * 0.004;
        } else {
            // 50세 이상: 매년 0.6%씩 감소
            return 0.9 - (age - 50) * 0.006;
        }
    }

    // 3. 혈중 농도 PK 모델
    public static double concentrationWithAbsorption(double t, double dose, double weight,
                                                     double tHalf, double ka) {
        double Vd = 0.65 * weight;
        double ke = Math.log(2) / tHalf;
        double F = 0.99; // 생체이용률(F=0.99 ≈ 1.0) 카페인은 거의 100% 흡수됨

        // 분모(ka-ke)가 0이 되는 문제 방지
        if (Math.abs(ka - ke) < 0.01) {
            return (F * dose / Vd) * t * ke * Math.exp(-ke * t);
        }

        // 기본 경구투여 모델 공식
        double factor = (F * dose * ka) / (Vd * (ka - ke));
        return factor * (Math.exp(-ke * t) - Math.exp(-ka * t));
    }

    // 오버로딩: ka 기본값 1.2
    public static double concentrationWithAbsorption(double t, double dose, double weight,
                                                     double tHalf) {
        return concentrationWithAbsorption(t, dose, weight, tHalf, 1.2);
    }

    // 4. PD 모델: caffeine_effect
    public static double caffeineEffect(double C, int age, int weekNumber, String effectType) {
        // 효과 종류에 따라 EC50 다르게 설정 (EC50 = 효과가 50% 나타나는 농도)
        Map<String, Double> ec50Values = new HashMap<>();
        ec50Values.put("alertness", 8.0);
        ec50Values.put("performance", 5.0);
        ec50Values.put("anxiety", 15.0);

        double EC50 = ec50Values.getOrDefault(effectType, 8.0);
        double Emax = 1.0;
        double hillCoefficient = 1.2;
        double basalEffect = 0.1; // 최대 효과, Hill coefficient(완만한 협동성), 기본 각성도

        // 내성, 민감도 -> 개인별 조정 -> '맞춤형' 기능 구현
        double tolerance = toleranceFactor(weekNumber, 0);
        double sensitivity = receptorSensitivity(age);

        double numerator = Emax * Math.pow(C, hillCoefficient);
        double denominator = Math.pow(EC50, hillCoefficient) + Math.pow(C, hillCoefficient);

        // 최종효과 = baseline 효과 + Hill 효과 × (내성 × 민감도)
        double effect = basalEffect + (numerator / denominator) * tolerance * sensitivity;

        return effect;
    }
}

