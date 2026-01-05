package com.softwaresecured.burp.threads;

import burp.api.montoya.collaborator.CollaboratorClient;
import burp.api.montoya.collaborator.Interaction;
import com.softwaresecured.burp.model.BurpSeleniumScripterModel;
import com.softwaresecured.burp.util.MontoyaUtil;

import java.time.Instant;

public class CollabMonitorThread extends Thread {
    private final int COLLAB_MONITOR_WAIT_MS = 1000;
    private BurpSeleniumScripterModel burpSeleniumScripterModel;
    private boolean shuttingDown = false;
    public CollabMonitorThread(BurpSeleniumScripterModel burpSeleniumScripterModel ) {
        this.burpSeleniumScripterModel = burpSeleniumScripterModel;
    }


    @Override
    public void run() {
        while ( !shuttingDown ) {
            try {
                CollaboratorClient client = getCollaboratorClient();
                if ( client != null ) {
                    for ( Interaction interaction : client.getAllInteractions() ) {
                        interaction.smtpDetails().ifPresent( smtpDetails -> {
                            if ( interaction.timeStamp().toEpochSecond() > burpSeleniumScripterModel.getLastInteractionTimestamp() ) {
                                burpSeleniumScripterModel.addInteraction(interaction);
                            }
                        });
                    }
                }
                burpSeleniumScripterModel.setLastCollaboratorPoll(Instant.now());
                Thread.sleep(COLLAB_MONITOR_WAIT_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private CollaboratorClient getCollaboratorClient() {
        if ( burpSeleniumScripterModel.getCollabSecret() != null ) {
            return MontoyaUtil.getApi().collaborator().restoreClient(burpSeleniumScripterModel.getCollabSecret());
        }
        return null;
    }

    public void shutdown() {
        shuttingDown = true;
    }
}
