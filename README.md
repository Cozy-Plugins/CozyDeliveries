# API

[![](https://jitpack.io/v/Cozy-Plugins/CozyDeliveries.svg)](https://jitpack.io/#Cozy-Plugins/CozyDeliveries)

```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

```xml
	<dependency>
	    <groupId>com.github.Cozy-Plugins</groupId>
	    <artifactId>CozyDeliveries</artifactId>
	    <version>Tag</version>
	</dependency>
```

# Example
```java
public CozyDeliveriesAPI getDeliveriesAPI() {
    return (CozyDeliveries) Bukkit.getServer().getPluginManager().getPlugin("CozyDeliveries");
}

public List<Delivery> getDeliveries(Player player) {
    return this.getDeliveriesAPI().getDeliveryList(player.getUniqueId());
}
```