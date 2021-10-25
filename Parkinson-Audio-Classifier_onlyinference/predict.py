import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'  # or any {'0', '1', '2'}
# NOTA: In caso di errori commentare la riga precedente

from tensorflow.keras.models import load_model
from convert import convert_mono_downsampling, convert_envelope
from kapre.time_frequency import STFT, Magnitude, ApplyFilterbank, MagnitudeToDecibel
from sklearn.preprocessing import LabelEncoder
import numpy as np
from glob import glob
import argparse
import os
import pandas as pd
from tqdm import tqdm


def do_prediction(args):

    model = load_model(args.model_fn, custom_objects={'STFT':STFT,
                        'Magnitude':Magnitude,
                        'ApplyFilterbank':ApplyFilterbank,
                        'MagnitudeToDecibel':MagnitudeToDecibel})

    audio_paths = glob('{}/**'.format(args.src_dir), recursive=True)
    audio_paths = sorted([x.replace(os.sep, '/') for x in audio_paths if '.wav' in x])
    classes = sorted(os.listdir(args.src_dir))
    labels = [os.path.split(x)[0].split('/')[-1] for x in audio_paths]
    encoded_labels = LabelEncoder()
    y_true = encoded_labels.fit_transform(labels)
    results = []

    for audio_file_path, audio_filename in tqdm(enumerate(audio_paths), total=len(audio_paths),disable=args.batch_mode):
        rate, wav = convert_mono_downsampling(audio_filename, args.sr)
        mask, env = convert_envelope(wav, rate, threshold=args.threshold)
        converted_wav = wav[mask]
        step = int(args.sr*args.dt)
        batch = []

        for i in range(0, converted_wav.shape[0], step):
            sample = converted_wav[i:i+step]
            sample = sample.reshape(-1, 1)
            if sample.shape[0] < step:
                tmp = np.zeros(shape=(step, 1), dtype=np.float32)
                tmp[:sample.shape[0],:] = sample.flatten().reshape(-1, 1)
                sample = tmp
            batch.append(sample)
        X_batch = np.array(batch, dtype=np.float32)
        y_pred = model.predict(X_batch,verbose=0)
        y_mean = np.mean(y_pred, axis=0)
        y_pred = np.argmax(y_mean)
        true_class = os.path.dirname(audio_filename).split('/')[-1]
        if not args.batch_mode:
            print('Actual class: {}, Predicted class: {}'.format(true_class, classes[y_pred]))
            results.append(y_mean)
        else:  # BATCH_MODE output
            print('{0},{1}'.format(audio_filename,classes[y_pred]))


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='Audio Classification Training')
    parser.add_argument('--model_fn', type=str, default='models/lstm.h5',
                        help='model file to make predictions')
    parser.add_argument('--batch_mode', type=bool, default=True,
                        help='Launch in batch mode just for inference (on unknown classes)')
    parser.add_argument('--pred_fn', type=str, default='y_pred',
                        help='fn to write predictions in logs dir')
    parser.add_argument('--src_dir', type=str,required=True, 
                        help='directory containing audio files to predict')
    parser.add_argument('--dt', type=float, default=1.0,
                        help='time in seconds to sample audio')
    parser.add_argument('--sr', type=int, default=16000,
                        help='sample rate of clean audio')
    parser.add_argument('--threshold', type=str, default=20,
                        help='threshold magnitude for np.int16 dtype')

    args, _ = parser.parse_known_args()

    do_prediction(args)

