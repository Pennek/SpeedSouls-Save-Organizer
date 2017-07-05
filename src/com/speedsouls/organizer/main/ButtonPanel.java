package com.speedsouls.organizer.main;


import java.awt.Component;
import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.speedsouls.organizer.components.ReadOnlyButton;
import com.speedsouls.organizer.components.SettingsButton;
import com.speedsouls.organizer.content.Game;
import com.speedsouls.organizer.content.Profile;
import com.speedsouls.organizer.content.Save;
import com.speedsouls.organizer.data.OrganizerManager;
import com.speedsouls.organizer.listeners.ProfileListener;
import com.speedsouls.organizer.listeners.SaveListener;


/**
 * Bottom segment of the main window.
 * <p>
 * Contains the import, load, read-only and help buttons.
 *
 * @author Kahmul (www.twitch.tv/kahmul78)
 * @date 27 Sep 2015
 */
public class ButtonPanel extends JPanel
{

	private static final long serialVersionUID = 4450835782973692167L;
	private JButton loadButton;
	private ReadOnlyButton readOnlyButton;
	private JProgressBar progressBar;


	/**
	 * Creates a new ButtonPanel for the main window.
	 */
	protected ButtonPanel()
	{
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		readOnlyButton = new ReadOnlyButton(OrganizerManager.getSelectedGame().getSaveFile(),
				new ImageIcon(OrganizerManager.readOnlyIconMedium));

		progressBar = createProgressBar();
		JButton importButton = createImportButton();
		loadButton = createLoadButton(readOnlyButton, progressBar);
		SettingsButton settingsButton = new SettingsButton();

		Component glue = Box.createHorizontalGlue();

		// Horizontal
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(importButton));

		hGroup.addGroup(layout.createParallelGroup().addComponent(loadButton));
		hGroup.addGroup(layout.createParallelGroup().addComponent(progressBar));
		hGroup.addGap(10);
		hGroup.addGroup(layout.createParallelGroup().addComponent(readOnlyButton));
		hGroup.addGroup(layout.createParallelGroup().addComponent(glue));
		hGroup.addGroup(layout.createParallelGroup().addComponent(settingsButton));

		layout.setHorizontalGroup(hGroup);

		// Vertical
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.CENTER).addComponent(importButton).addComponent(loadButton)
				.addComponent(progressBar).addComponent(readOnlyButton).addComponent(glue).addComponent(settingsButton));
		vGroup.addGap(10);

		layout.setVerticalGroup(vGroup);

		setLayout(layout);

		addLoadButtonListeners(loadButton);
	}


	/**
	 * Creates the progressbar next to the load button.
	 * 
	 * @return the progressbar
	 */
	private JProgressBar createProgressBar()
	{
		JProgressBar progressBar = new JProgressBar();
		progressBar.setMaximumSize(new Dimension(50, 15));
		return progressBar;
	}


	/**
	 * Creates the 'Import current savefile' button.
	 * 
	 * @return the import button
	 */
	private JButton createImportButton()
	{
		JButton importButton = new JButton("Import current savefile");
		importButton.addActionListener(event -> {
			Profile profile = OrganizerManager.getSelectedProfile();
			if (profile.getDirectory().exists())
			{
				Game game = OrganizerManager.getSelectedGame();
				OrganizerManager.importAsSave(game.getSaveFile(), profile);
				return;
			}
			JOptionPane.showMessageDialog(null, "Create a profile before trying to import a savefile!", "Error occured",
					JOptionPane.ERROR_MESSAGE);
		});
		return importButton;
	}


	/**
	 * Creates the 'Load selected savefile' button.
	 * 
	 * @param readOnlyButton the read-only button of this panel
	 * @return the load button
	 */
	private JButton createLoadButton(ReadOnlyButton readOnlyButton, JProgressBar progressBar)
	{
		JButton loadButton = new JButton("Load selected savefile");
		loadButton.addActionListener(event -> {
			Save save = OrganizerManager.getSelectedSave();
			if (save.isDirectory())
				return;
			OrganizerManager.loadSave(save);
		});
		loadButton.setEnabled(false);
		return loadButton;
	}


	/**
	 * Adds the listeners to enable/disable the load button.
	 * 
	 * @param loadButton the load button
	 */
	private void addLoadButtonListeners(JButton loadButton)
	{
		OrganizerManager.addSaveListener(new SaveListener() {

			@Override
			public void saveSelected(Save save)
			{
				if (save != null)
				{
					loadButton.setEnabled(!save.isDirectory());
					return;
				}
				loadButton.setEnabled(false);
			}


			@Override
			public void saveRenamed(Save save, String newName)
			{
			}


			@Override
			public void saveLoadStarted(Save save)
			{
				progressBar.setIndeterminate(true);
			}


			@Override
			public void saveLoadFinished(Save save)
			{
				readOnlyButton.setFile(OrganizerManager.getSelectedGame().getSaveFile());
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run()
					{
						progressBar.setIndeterminate(false);
					}
				}, 200);
			}


			@Override
			public void gameFileWritableStateChanged(boolean writable)
			{
			}
		});

		OrganizerManager.addProfileListener(new ProfileListener() {

			@Override
			public void profilesUpdated(Game game)
			{
			}


			@Override
			public void changedToProfile(Profile profile)
			{
				loadButton.setEnabled(false);
			}


			@Override
			public void changedToGame(Game game)
			{
				loadButton.setEnabled(false);
			}


			@Override
			public void addedToProfile(Save save, Profile profile)
			{
			}


			@Override
			public void removedFromProfile(Save save, Profile profile)
			{
			}
		});
	}
}