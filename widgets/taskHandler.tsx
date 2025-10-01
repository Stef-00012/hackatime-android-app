import React from 'react';
import type { WidgetTaskHandlerProps } from 'react-native-android-widget';
import { TestWidget } from './test/Widget';

const nameToWidget = {
  // Hello will be the **name** with which we will reference our widget.
  Hello: TestWidget,
};

export async function widgetTaskHandler(props: WidgetTaskHandlerProps) {
  const widgetInfo = props.widgetInfo;
  const Widget =
    nameToWidget[widgetInfo.widgetName as keyof typeof nameToWidget];

  switch (props.widgetAction) {
    case 'WIDGET_ADDED':
      console.log('WIDGET_ADDED')
      props.renderWidget(<Widget />);
      break;

    case 'WIDGET_UPDATE':
      console.log('WIDGET_UPDATE')
      // Not needed for now
      break;

    case 'WIDGET_RESIZED':
      console.log('WIDGET_RESIZED')
      // Not needed for now
      break;

    case 'WIDGET_DELETED':
      console.log('WIDGET_DELETED')
      // Not needed for now
      break;

    case 'WIDGET_CLICK':
      console.log('WIDGET_CLICK')
      // Not needed for now
      break;

    default:
      break;
  }
}