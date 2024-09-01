package com.visualization.fetch;

import com.visualization.auth.message.AuthMessage;

public interface AuthMessageConsumer {

    void consume(AuthMessage message);
}
