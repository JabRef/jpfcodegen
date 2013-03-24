package com.example;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.boot.DefaultPluginsCollector;
import org.java.plugin.util.ExtendedProperties;

import com.example.core.CorePlugin;
import com.example.core.generated._CorePlugin.PanelExtension;

public class Main {

	public static void main(final String[] args) {
		// Move to non-static and swing-thread.
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				new Main().start(args);
			}
		});
	}

	PluginManager manager;

	public void start(String[] args) {

		// Create Plugin Manager
		manager = ObjectFactory.newInstance().createManager();

		// Find plugins and add to registry
		DefaultPluginsCollector collector = new DefaultPluginsCollector();
		ExtendedProperties ep = new ExtendedProperties();
		ep.setProperty("org.java.plugin.boot.pluginsRepositories",
				"./plugins");
		try {
			collector.configure(ep);
			manager.publishPlugins(collector.collectPluginLocations().toArray(
					new PluginLocation[] {}));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		// Plugin system is now ready to use...
		
		// Start the application
		JFrame frame = new JFrame("JPF Code Generator Basic Tutorial");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 300);
		frame.setLocation(300, 200);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		frame.add(tabbedPane);

		// Use the plugin system to find panels to add to the tabbed pane
		CorePlugin corePlugin = CorePlugin.getInstance(manager);
		
		// corePlugin might be null here (for instance if the plug-in is missing)
		if (corePlugin != null){
			
			// Now we can get all extensions for the Panel extension point
			for (PanelExtension extension : corePlugin.getPanelExtensions()){
				
				// The PanelExtension class then allows easy access to the parameters defined 
				// for this extension-point. 
				JPanel panel = extension.getPanel();
				String name = extension.getName();
		
				// panel might be null if an error occurred while creating the object
				if (panel == null){
					System.out.println("Error loading panel from extension " + extension.getId());
					continue;
				}
				
				tabbedPane.addTab(name, panel);
			}
		}

		frame.setVisible(true);
	}
}
