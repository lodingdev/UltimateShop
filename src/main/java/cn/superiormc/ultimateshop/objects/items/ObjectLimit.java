package cn.superiormc.ultimateshop.objects.items;

import cn.superiormc.ultimateshop.utils.MathUtil;
import cn.superiormc.ultimateshop.utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectLimit {

    private ConfigurationSection limitSection;

    private ConfigurationSection conditionsSection;

    public ObjectLimit() {
        // Empty
    }

    public ObjectLimit(ConfigurationSection limitSection, ConfigurationSection conditionsSection) {
        this.limitSection = limitSection;
        this.conditionsSection = conditionsSection;
    }

    public int getPlayerLimits(Player player) {
        if (limitSection == null) {
            return -1;
        }
        if (conditionsSection == null) {
            return (int) MathUtil.doCalculate(
                    TextUtil.withPAPI(limitSection.getString("default", "-1"), player));
        }
        List<Integer> result = new ArrayList<>();
        for (String conditionName : limitSection.getKeys(false)) {
            if (!conditionName.equals("default") && checkLimitsCondition(conditionName, player)) {
                result.add((int) MathUtil.doCalculate(
                        TextUtil.withPAPI(limitSection.getString(conditionName, "-1"), player)));
            }
            else {
                if (!limitSection.getString("default", "-1").equals("-1")) {
                    result.add((int) MathUtil.doCalculate(
                            TextUtil.withPAPI(limitSection.getString("default"), player)));
                }
            }
        }
        if (result.size() == 0) {
            result.add(0);
        }
        return Collections.max(result);
    }

    public int getServerLimits(Player player) {
        if (limitSection == null) {
            return -1;
        }
        int tempVal2 = -1;
        if (limitSection.getDouble("global", -1) != -1) {
            tempVal2 = ((int) MathUtil.doCalculate(
                    TextUtil.withPAPI(limitSection.getString("global"), player)));
        }
        return tempVal2;
    }

    private boolean checkLimitsCondition(String conditionName, Player player) {
        List<String> condition;
        if (!conditionsSection.getStringList(conditionName).isEmpty()) {
            condition = conditionsSection.getStringList(conditionName);
            ObjectCondition tempVal1;
            if (condition.isEmpty()) {
                tempVal1 = new ObjectCondition();
            }
            else {
                tempVal1 = new ObjectCondition(condition);
            }
            return tempVal1.getBoolean(player);
        }
        else {
            return false;
        }
    }
}
