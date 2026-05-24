package com.smarthome.config;

import com.smarthome.entity.DeviceEntity;
import com.smarthome.model.Device;
import com.smarthome.model.Room;
import com.smarthome.repository.DeviceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DeviceRepository deviceRepository;

    public DataInitializer(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public void run(String... args) {
        if (deviceRepository.count() > 0) return;

        List<Room> rooms = buildRooms();
        List<DeviceEntity> entities = new ArrayList<>();
        for (Room room : rooms) {
            for (Device d : room.getDevices()) {
                entities.add(DeviceEntity.fromDevice(d, room.getName(), room.getIcon(),
                    room.getDesc(), room.getIconClass()));
            }
        }
        deviceRepository.saveAll(entities);
    }

    private List<Room> buildRooms() {
        Room living = new Room();
        living.setId("living"); living.setName("客厅"); living.setIcon("🛋");
        living.setDesc("家庭活动与娱乐中心"); living.setIconClass("living");
        living.setDevices(new ArrayList<>(List.of(
            dev("living-light", "智能吊灯", "💡", "light", true, "客厅", "lb", 12, m -> { m.put("brightness",80); m.put("color","#ffeaa7"); }),
            dev("living-tv", "智能电视", "📺", "tv", false, "客厅", "cb", 120, m -> { m.put("volume",30); m.put("input","HDMI 1"); }),
            dev("living-ac", "中央空调", "❄️", "ac", true, "客厅", "cb", 2200, m -> { m.put("temp",24); m.put("mode","制冷"); }),
            dev("living-curtain", "电动窗帘", "🪟", "curtain", true, "客厅", "gb", 30, m -> { m.put("openPercent",70); }),
            dev("living-speaker", "智能音箱", "🔊", "speaker", true, "客厅", "pb", 15, m -> { m.put("volume",40); m.put("playing","轻音乐"); })
        )));

        Room bedroom = new Room();
        bedroom.setId("bedroom"); bedroom.setName("卧室"); bedroom.setIcon("🛏");
        bedroom.setDesc("休息与睡眠空间"); bedroom.setIconClass("bedroom");
        bedroom.setDevices(new ArrayList<>(List.of(
            dev("bedroom-light","床头灯","💡","light",false,"卧室","lb",8, m->{ m.put("brightness",30); m.put("color","#fd79a8"); }),
            dev("bedroom-ac","空调","❄️","ac",true,"卧室","cb",1800, m->{ m.put("temp",26); m.put("mode","睡眠"); }),
            dev("bedroom-curtain","电动窗帘","🪟","curtain",false,"卧室","gb",30, m->{ m.put("openPercent",0); }),
            dev("bedroom-humidifier","加湿器","💨","humidifier",true,"卧室","cb",25, m->{ m.put("humidity",55); m.put("waterLevel",70); })
        )));

        Room kitchen = new Room();
        kitchen.setId("kitchen"); kitchen.setName("厨房"); kitchen.setIcon("🍳");
        kitchen.setDesc("烹饪与餐饮空间"); kitchen.setIconClass("kitchen");
        kitchen.setDevices(new ArrayList<>(List.of(
            dev("kitchen-light","厨卫灯","💡","light",true,"厨房","lb",10, m->{ m.put("brightness",100); m.put("color","#ffffff"); }),
            dev("kitchen-fridge","智能冰箱","🧊","fridge",true,"厨房","cb",150, m->{ m.put("tempFridge",4); m.put("tempFreezer",-18); m.put("mode","智能"); }),
            dev("kitchen-oven","智能烤箱","🔥","oven",false,"厨房","wb",2800, m->{ m.put("temp",180); }),
            dev("kitchen-detector","烟雾探测器","🚨","detector",true,"厨房","wb",null, m->{ m.put("alarm",false); m.put("battery",95); })
        )));

        Room bathroom = new Room();
        bathroom.setId("bathroom"); bathroom.setName("卫生间"); bathroom.setIcon("🛁");
        bathroom.setDesc("洗浴与清洁空间"); bathroom.setIconClass("bathroom");
        bathroom.setDevices(new ArrayList<>(List.of(
            dev("bathroom-light","浴室灯","💡","light",true,"卫生间","lb",10, m->{ m.put("brightness",60); m.put("color","#81ecec"); }),
            dev("bathroom-fan","排气扇","🌀","fan",true,"卫生间","cb",35, m->{ m.put("speed",2); }),
            dev("bathroom-heater","浴霸","☀️","heater",false,"卫生间","wb",2000, m->{ m.put("temp",30); }),
            dev("bathroom-water-heater","热水器","🔥","water-heater",true,"卫生间","wb",3000, m->{ m.put("tempSet",45); })
        )));

        Room door = new Room();
        door.setId("door"); door.setName("门禁安防"); door.setIcon("🚪");
        door.setDesc("入户与安全监控"); door.setIconClass("door");
        door.setDevices(new ArrayList<>(List.of(
            dev("door-lock","智能门锁","🔒","lock",true,"门禁","gb",null, m->{ m.put("locked",true); m.put("battery",80); m.put("lastUnlock","今早 08:15"); }),
            dev("doorbell","可视门铃","📹","doorbell",true,"门禁","pb",null, m->{ m.put("battery",65); m.put("lastEvent","30分钟前有人经过"); }),
            dev("door-sensor","门窗传感器","📡","sensor",true,"门禁","cb",null, m->{ m.put("closed",true); m.put("battery",90); }),
            dev("door-camera","室外摄像头","🎥","camera",true,"门禁","pb",null, m->{ m.put("recording",true); m.put("resolution","4K"); m.put("storage","65% 已用"); })
        )));

        return List.of(living, bedroom, kitchen, bathroom, door);
    }

    private Device dev(String id, String name, String icon, String type, boolean state,
                       String room, String iconBg, Integer power,
                       java.util.function.Consumer<Map<String,Object>> extra) {
        Device d = new Device();
        d.setId(id); d.setName(name); d.setIcon(icon); d.setType(type);
        d.setState(state); d.setRoom(room); d.setIconBg(iconBg); d.setPower(power);
        Map<String,Object> m = new HashMap<>();
        extra.accept(m);
        m.forEach((k,v) -> {
            if (v instanceof Boolean) { if (k.equals("state")) d.setState((Boolean)v); else if (k.equals("locked")) d.setLocked((Boolean)v); else if (k.equals("alarm")) d.setAlarm((Boolean)v); else if (k.equals("recording")) d.setRecording((Boolean)v); else if (k.equals("closed")) d.setClosed((Boolean)v); }
            else if (v instanceof Integer) { if (k.equals("brightness")) d.setBrightness((Integer)v); else if (k.equals("temp")) d.setTemp((Integer)v); else if (k.equals("openPercent")) d.setOpenPercent((Integer)v); else if (k.equals("volume")) d.setVolume((Integer)v); else if (k.equals("speed")) d.setSpeed((Integer)v); else if (k.equals("tempSet")) d.setTempSet((Integer)v); else if (k.equals("power")) d.setPower((Integer)v); else if (k.equals("humidity")) d.setHumidity((Integer)v); else if (k.equals("waterLevel")) d.setWaterLevel((Integer)v); else if (k.equals("battery")) d.setBattery((Integer)v); else if (k.equals("tempFridge")) d.setTempFridge((Integer)v); else if (k.equals("tempFreezer")) d.setTempFreezer((Integer)v); }
            else if (v instanceof String) { if (k.equals("color")) d.setColor((String)v); else if (k.equals("mode")) d.setMode((String)v); else if (k.equals("input")) d.setInput((String)v); else if (k.equals("playing")) d.setPlaying((String)v); else if (k.equals("resolution")) d.setResolution((String)v); else if (k.equals("storage")) d.setStorage((String)v); else if (k.equals("lastUnlock")) d.setLastUnlock((String)v); else if (k.equals("lastEvent")) d.setLastEvent((String)v); }
        });
        return d;
    }
}
