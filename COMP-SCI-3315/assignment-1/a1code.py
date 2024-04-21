
### Supporting code for Computer Vision Assignment 1
### See "Assignment 1.ipynb" for instructions

import math

import numpy as np
from skimage import io

def load(img_path):
    """Loads an image from a file path.

    HINT: Look up `skimage.io.imread()` function.
    HINT: Converting all pixel values to a range between 0.0 and 1.0
    (i.e. divide by 255) will make your life easier later on!

    Inputs:
        image_path: file path to the image.

    Returns:
        out: numpy array of shape(image_height, image_width, 3).
    """

    out = io.imread(img_path)

    out = out.astype(float) / 255.0

    return out

def print_stats(image):
    """ Prints the height, width and number of channels in an image.

    Inputs:
        image: numpy array of shape(image_height, image_width, n_channels).

    Returns: none

    """

    # YOUR CODE HERE
    if len(image.shape) == 3:
        print("Height: ", image.shape[0])
        print("Width: ", image.shape[1])
        print("Channels: ", image.shape[2])
    else:
        channels = 1
        print("Height: ", image.shape[0])
        print("Width: ", image.shape[1])
        print("Channels: ", channels)

    return None

def crop(image, start_row, start_col, num_rows, num_cols):
    """Crop an image based on the specified bounds. Use array slicing.

    Inputs:
        image: numpy array of shape(image_height, image_width, 3).
        start_row (int): The starting row index
        start_col (int): The starting column index
        num_rows (int): Number of rows in our cropped image.
        num_cols (int): Number of columns in our cropped image.

    Returns:
        out: numpy array of shape(num_rows, num_cols, 3).
    """

    end_row = start_row + num_rows
    end_col = start_col + num_cols

    out = image[start_row:end_row, start_col:end_col, :]

    return out

def resize(input_image, output_rows, output_cols):
    """Resize an image using the nearest neighbor method.
    i.e. for each output pixel, use the value of the nearest input pixel after scaling

    Inputs:
        input_image: RGB image stored as an array, with shape
            `(input_rows, input_cols, 3)`.
        output_rows (int): Number of rows in our desired output image.
        output_cols (int): Number of columns in our desired output image.

    Returns:
        np.ndarray: Resized image, with shape `(output_rows, output_cols, 3)`.
    """

    old_num_rows, old_num_cols = input_image.shape

    if output_rows <= 0 or output_cols <= 0:
        raise ValueError("Output dimensions must be positive integers.")
    if output_rows > old_num_rows or output_cols > old_num_cols:
        raise ValueError("Output dimensions exceed input image dimensions.")

    row_scale = old_num_rows / output_rows
    col_scale = old_num_cols / output_cols

    row_indices = np.arange(output_rows) * row_scale
    col_indices = np.arange(output_cols) * col_scale

    row_indices = np.round(row_indices).astype(int)
    col_indices = np.round(col_indices).astype(int)

    out = input_image[row_indices][:, col_indices]

    return out

def change_contrast(image, factor):
    """Change the value of every pixel by following

                        x_n = factor * (x_p - 0.5) + 0.5

    where x_n is the new value and x_p is the original value.
    Assumes pixel values between 0.0 and 1.0
    If you are using values 0-255, change 0.5 to 128.

    Inputs:
        image: numpy array of shape(image_height, image_width, 3).
        factor (float): contrast adjustment

    Returns:
        out: numpy array of shape(image_height, image_width, 3).
    """
    new_values = factor * (image - 0.5) + 0.5

    out = np.clip(new_values, 0, 1)

    return out

def greyscale(input_image):
    """Convert a RGB image to greyscale.
    A simple method is to take the average of R, G, B at each pixel.
    Or you can look up more sophisticated methods online.

    Inputs:
        input_image: RGB image stored as an array, with shape
            `(input_rows, input_cols, 3)`.

    Returns:
        np.ndarray: Greyscale image, with shape `(output_rows, output_cols)`.
    """
    out = np.mean(input_image, axis=2)

    return out

def binary(grey_img, th):
    """Convert a greyscale image to a binary mask with threshold.

                  x_n = 0, if x_p < th
                  x_n = 1, if x_p >= th

    Inputs:
        input_image: Greyscale image stored as an array, with shape
            `(image_height, image_width)`.
        th (float): The threshold used for binarization, and the value range is 0 to 1
    Returns:
        np.ndarray: Binary mask, with shape `(image_height, image_width)`.
    """

    out = grey_img >= th

    return out

def conv2D(image, kernel):
    """ Convolution of a 2D image with a 2D kernel.
    Convolution is applied to each pixel in the image.
    Assume values outside image bounds are 0.

    Args:
        image: numpy array of shape (Hi, Wi).
        kernel: numpy array of shape (Hk, Wk). Dimensions will be odd.

    Returns:
        out: numpy array of shape (Hi, Wi).
    """
    out = None
    ### YOUR CODE HERE

    kernel = np.flipud(np.fliplr(kernel))

    Hi, Wi = image.shape
    Hk, Wk = kernel.shape

    height_padding = Hk // 2
    width_padding = Wk // 2

    padded_image = np.pad(image, ((height_padding, height_padding), (width_padding, width_padding)), mode='constant')

    out = np.zeros_like(image)

    for i in range(Hi):
        for j in range(Wi):
            region = padded_image[i:i+Hk, j:j+Wk]
            inital = np.sum(region * kernel)
            out[i, j] = np.clip(inital, 0, 1)


    return out

def test_conv2D():
    """ A simple test for your 2D convolution function.
        You can modify it as you like to debug your function.

    Returns:
        None
    """

    # Test code written by
    # Simple convolution kernel.
    kernel = np.array(
    [
        [1,0,1],
        [0,0,0],
        [1,0,0]
    ])

    # Create a test image: a white square in the middle
    test_img = np.zeros((9, 9))
    test_img[3:6, 3:6] = 1

    # Run your conv_nested function on the test image
    test_output = conv2D(test_img, kernel)

    # Build the expected output
    expected_output = np.zeros((9, 9))
    expected_output[2:7, 2:7] = 1
    expected_output[5:, 5:] = 0
    expected_output[4, 2:5] = 2
    expected_output[2:5, 4] = 2
    expected_output[4, 4] = 3

    # Test if the output matches expected output
    assert np.max(test_output - expected_output) < 1e-10, "Your solution is not correct."


def conv(image, kernel):
    """Convolution of a RGB or grayscale image with a 2D kernel

    Args:
        image: numpy array of shape (Hi, Wi, 3) or (Hi, Wi)
        kernel: numpy array of shape (Hk, Wk). Dimensions will be odd.

    Returns:
        out: numpy array of shape (Hi, Wi, 3) or (Hi, Wi)
    """
    out = None
    ### YOUR CODE HERE

    if len(image.shape) == 3:
        out = np.zeros_like(image)

        for i in range(3):
            test = conv2D(image[:, :, i], kernel)
            out[:, :, i] = test
    else:
        out = conv2D(image, kernel)


    return out


def gauss2D(size, sigma):

    """Function to mimic the 'fspecial' gaussian MATLAB function.
       You should not need to edit it.

    Args:
        size: filter height and width
        sigma: std deviation of Gaussian

    Returns:
        numpy array of shape (size, size) representing Gaussian filter
    """

    x, y = np.mgrid[-size//2 + 1:size//2 + 1, -size//2 + 1:size//2 + 1]
    g = np.exp(-((x**2 + y**2)/(2.0*sigma**2)))
    return g/g.sum()


def corr(image, kernel):
    """Cross correlation of a RGB image with a 2D kernel

    Args:
        image: numpy array of shape (Hi, Wi, 3) or (Hi, Wi)
        kernel: numpy array of shape (Hk, Wk). Dimensions will be odd.

    Returns:
        out: numpy array of shape (Hi, Wi, 3) or (Hi, Wi)
    """
    out = None
    ### YOUR CODE HERE

    return out

