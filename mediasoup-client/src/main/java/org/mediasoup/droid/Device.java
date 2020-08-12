package org.mediasoup.droid;

import android.util.Log;

import org.webrtc.PeerConnectionFactory;

public class Device {
  private final static String TAG = "DROID_DEVICE";

  private long mNativeDevice;
  private PeerConnectionFactory pcFactory;

  public Device() {
    mNativeDevice = nativeNewDevice();
  }

  public Device(PeerConnectionFactory pcFactory){
    this.pcFactory = pcFactory;
    mNativeDevice = nativeNewDevice();
  }

  public void dispose() {
    this.pcFactory = null;
    checkDeviceExists();
    nativeFreeDevice(mNativeDevice);
    mNativeDevice = 0;
  }

  public void load(String routerRtpCapabilities) throws MediasoupException {
    checkDeviceExists();
    nativeLoad(mNativeDevice, routerRtpCapabilities);
  }

  public boolean isLoaded() {
    checkDeviceExists();
    return nativeIsLoaded(mNativeDevice);
  }

  public String getRtpCapabilities() throws MediasoupException {
    checkDeviceExists();
    return nativeGetRtpCapabilities(mNativeDevice);
  }

  public boolean canProduce(String kind) throws MediasoupException {
    checkDeviceExists();
    return nativeCanProduce(mNativeDevice, kind);
  }

  public SendTransport createSendTransport(
      SendTransport.Listener listener,
      String id,
      String iceParameters,
      String iceCandidates,
      String dtlsParameters)
      throws MediasoupException {
    Log.e(TAG, "createSendTransport: Creating sendTransport without pcFactory");
    return createSendTransport(
        listener, id, iceParameters, iceCandidates, dtlsParameters, null, null);
  }

  public SendTransport createSendTransport(
      SendTransport.Listener listener,
      String id,
      String iceParameters,
      String iceCandidates,
      String dtlsParameters,
      PeerConnection.Options options,
      String appData)
      throws MediasoupException {
    checkDeviceExists();

    if(options == null || options.mFactory == null){
      Log.e(TAG, "createSendTransport2: Creating sendTransport without pcFactory");
    }

    return nativeCreateSendTransport(
        mNativeDevice,
        listener,
        id,
        iceParameters,
        iceCandidates,
        dtlsParameters,
        (options != null ? options.mRTCConfig : null),
        (options != null && options.mFactory != null
            ? options.mFactory.getNativePeerConnectionFactory()
            : 0L),
        appData);
  }

  public RecvTransport createRecvTransport(
      RecvTransport.Listener listener,
      String id,
      String iceParameters,
      String iceCandidates,
      String dtlsParameters,
      String appData)
      throws MediasoupException {
    Log.e(TAG, "createRecvTransport1: Creating recvTransport without pcFactory");
    return createRecvTransport(
        listener, id, iceParameters, iceCandidates, dtlsParameters, null, appData);
  }

  public RecvTransport createRecvTransport(
      RecvTransport.Listener listener,
      String id,
      String iceParameters,
      String iceCandidates,
      String dtlsParameters,
      PeerConnection.Options options,
      String appData)
      throws MediasoupException {
    checkDeviceExists();

    if(options == null || options.mFactory == null){
      Log.e(TAG, "createRecvTransport2: Creating recvTransport without pcFactory");
    }

    return nativeCreateRecvTransport(
        mNativeDevice,
        listener,
        id,
        iceParameters,
        iceCandidates,
        dtlsParameters,
        (options != null ? options.mRTCConfig : null),
        (options != null && options.mFactory != null
            ? options.mFactory.getNativePeerConnectionFactory()
            : 0L),
        appData);
  }

  private void checkDeviceExists() {
    if (mNativeDevice == 0) {
      throw new IllegalStateException("Device has been disposed.");
    }
  }

  private static native long nativeNewDevice();

  private static native void nativeFreeDevice(long device);

  // may throws MediasoupException;
  private static native void nativeLoad(long device, String routerRtpCapabilities);

  private static native boolean nativeIsLoaded(long device);

  // may throws MediasoupException;
  private static native String nativeGetRtpCapabilities(long device);

  // may throws MediasoupException;
  private static native boolean nativeCanProduce(long device, String kind);

  // may throws MediasoupException;
  private static native SendTransport nativeCreateSendTransport(
      long device,
      SendTransport.Listener listener,
      String id,
      String iceParameters,
      String iceCandidates,
      String dtlsParameters,
      org.webrtc.PeerConnection.RTCConfiguration configuration,
      long peerConnectionFactory,
      String appData);

  // may throws MediasoupException;
  private static native RecvTransport nativeCreateRecvTransport(
      long device,
      RecvTransport.Listener listener,
      String id,
      String iceParameters,
      String iceCandidates,
      String dtlsParameters,
      org.webrtc.PeerConnection.RTCConfiguration configuration,
      long peerConnectionFactory,
      String appData);
}
