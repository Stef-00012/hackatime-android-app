// import React from 'react';
import { FlexWidget, TextWidget } from 'react-native-android-widget';

const _WIDGET_NAME = "test";

export function Widget() {
    "use no memo";

    return (
        <FlexWidget
            style={{
                height: 'match_parent',
                width: 'match_parent',
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: '#ffffff',
                borderRadius: 16,
            }}
        >
            <TextWidget
                text="Test Widget"
                style={{
                    fontSize: 32,
                    fontFamily: 'PhantomSans-Regular',
                    color: '#000000',
                }}
            />
        </FlexWidget>
    );
}