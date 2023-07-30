import 'dart:convert';
import 'dart:io';
import 'package:geolocator/geolocator.dart';

import 'package:flutter/material.dart';
import 'package:flutter_quill/flutter_quill.dart' as quill hide Text;
import 'package:flutter_quill_extensions/flutter_quill_extensions.dart';
import 'package:provider/provider.dart';
import 'package:used_market/data_types/constants_data.dart';
import 'package:vsc_quill_delta_to_html/vsc_quill_delta_to_html.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'package:image_picker/image_picker.dart';
import 'package:used_market/data_types/board_data.dart';

class BoardInsert extends StatefulWidget {
  const BoardInsert({Key? key}) : super(key: key);

  @override
  State<BoardInsert> createState() => _BoardInsertState();
}

class _BoardInsertState extends State<BoardInsert> {
  Future<LocationPermission>? permissionStatus;
  final imagePicker = ImagePicker();
  final FlutterSecureStorage _secureStorage = FlutterSecureStorage();
  final quill.QuillController _quillController = quill.QuillController.basic();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final TextEditingController _titleController = TextEditingController();
  final TextEditingController _productNameController = TextEditingController();
  final TextEditingController _priceController = TextEditingController();

  final storage = FlutterSecureStorage();

  double longitude = 0.0;
  double latitude = 0.0;


  @override
  void initState() {
    super.initState();
    permissionStatus = _checkPermission();
  }

  Future<LocationPermission> _checkPermission() async {
    final isLocationEnabled = await Geolocator.isLocationServiceEnabled();

    if (!isLocationEnabled) {
      throw Exception('위치 서비스를 활성화해주세요.');
    }

    LocationPermission checkedPermission = await Geolocator.checkPermission();

    if (checkedPermission == LocationPermission.denied) {
      checkedPermission = await Geolocator.requestPermission();

      if (checkedPermission == LocationPermission.denied) {
        throw Exception('위치 권한을 허가해주세요.');
      }
    }

    return checkedPermission;
  }

  Future<void> _getCurrentPosition() async {
    final currentPosition = await Geolocator.getCurrentPosition();
    longitude = currentPosition.longitude;
    latitude = currentPosition.latitude;
  }

  Future<String> _pickImageAndUpload(int kind) async {
    final ImagePicker _picker = ImagePicker();

    final XFile? image = kind == 1
        ? await _picker.pickImage(source: ImageSource.gallery)
        : await _picker.pickImage(source: ImageSource.camera);

    if (image == null) {
      return "";
    }

    final http.MultipartRequest request = http.MultipartRequest(
      'POST',
      Uri.parse('${Constants.api_gate_url}/imgfile-service/uploadimg'),
    );

    request.files.add(await http.MultipartFile.fromPath(
      'file',
      image.path,
    ));

    final http.StreamedResponse streamedResponse = await request.send();

    final http.Response response =
        await http.Response.fromStream(streamedResponse);

    if (response.statusCode == 200) {
      print('Uploaded!');
      Map<String, dynamic> responseBody = jsonDecode(response.body);
      print('Response body: $responseBody');
      print('Result: ${responseBody['result']}');
      return '${responseBody['result']}';
    } else {
      print('Failed to upload image: ${response.statusCode}');
      print('Response body: ${response.body}');
      throw Exception("업로드 실패");
    }
  }

  Future<void> _pickSellImage(int kind, ImageProvider imageProvider) async {
    imageProvider.imageFile = kind == 1
        ? await imagePicker.pickImage(source: ImageSource.gallery)
        : await imagePicker.pickImage(source: ImageSource.camera);

  }

  void insertImage(quill.QuillController controller, String imageUrl) {
    final index = controller.selection.baseOffset;
    var imageEmbeddable = quill.BlockEmbed.image(imageUrl);
    controller.document.insert(index, imageEmbeddable);
    controller.updateSelection(
        TextSelection.collapsed(offset: index + 1), quill.ChangeSource.LOCAL);
  }



  Future<BoardResponse> insertBoard(ImageProvider imageProvider) async {
    var multipartRequest = http.MultipartRequest(
        'POST', Uri.parse('${Constants.api_gate_url}/board-service/boards'));
    final bearerToken = 'Bearer ${await _secureStorage.read(key: 'jwt')}';
    print(bearerToken);
    final deltaString = jsonEncode(_quillController.document.toDelta().toJson());
    final List<Map<String, dynamic>> deltaList = _quillController.document
        .toDelta()
        .toJson() as List<Map<String, dynamic>>;
    final QuillDeltaToHtmlConverter convertData =
        QuillDeltaToHtmlConverter(deltaList, ConverterOptions.forEmail());
    String htmlString = convertData.convert();

    if (imageProvider.imageFile != null) {
      multipartRequest.files.add(await http.MultipartFile.fromPath(
          'imgFile', imageProvider.imageFile!.path));
    }

    multipartRequest.headers.addAll(
      {'Authorization' : bearerToken}
    );

    multipartRequest.fields['longitude'] = longitude.toString();
    multipartRequest.fields['latitude'] = latitude.toString();
    multipartRequest.fields['title'] = _titleController.text;
    multipartRequest.fields['price'] = _priceController.text;
    multipartRequest.fields['productName'] = _productNameController.text;
    multipartRequest.fields['htmlString'] = htmlString;
    multipartRequest.fields['deltaString'] = deltaString;


    print(htmlString);
    print(deltaString);

    final http.StreamedResponse streamedResponse =
        await multipartRequest.send();

    final http.Response response =
        await http.Response.fromStream(streamedResponse);

    if (response.statusCode == 200) {
      print('Uploaded!');
      Map<String, dynamic> responseBody = jsonDecode(response.body);
      print('Response body: $responseBody');
      return BoardResponse.fromJson(responseBody);
    } else {
      print('Failed to upload image: ${response.statusCode}');
      print('Response body: ${response.body}');
      throw Exception("업로드 실패");
    }
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<LocationPermission>(
        future: permissionStatus,
        builder:
            (BuildContext context, AsyncSnapshot<LocationPermission> snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return CircularProgressIndicator(); // or other loading widget
          } else if (snapshot.hasError) {
            return Text('Error: ${snapshot.error}');
          } else if (snapshot.data != LocationPermission.whileInUse &&
              snapshot.data != LocationPermission.always) {
            return Text('위치 권한을 허가해주세요.');
          } else {
            return FutureBuilder<void>(
                future: _getCurrentPosition(),
                builder: (BuildContext context, AsyncSnapshot<void> snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return CircularProgressIndicator(); // or other loading widget
                  } else if (snapshot.hasError) {
                    return Text('Error: ${snapshot.error}');
                  } else {
                    return ChangeNotifierProvider(
                      create: (_) => ImageProvider(),
                      child: Column(
                        children: [
                          Row(
                            children: [
                              Container(
                                padding: EdgeInsets.all(5),
                                width: MediaQuery.of(context).size.width * 0.77,
                                height:
                                    MediaQuery.of(context).size.height * 0.08,
                                child: TextFormField(
                                  controller: _titleController,
                                  decoration:
                                      const InputDecoration(labelText: '제목'),
                                  validator: (String? value) {
                                    if (value == null || value.isEmpty) {
                                      return '제목을 입력하세요';
                                    }
                                    return null;
                                  },
                                ),
                              ),
                              Consumer<ImageProvider>(
                                builder: (context, imageProvider, child) {
                                  return ElevatedButton(
                                    onPressed: () {
                                      if (_formKey.currentState!.validate()) {
                                        insertBoard(imageProvider);
                                      }
                                    },
                                    child: Text('작성 완료'),
                                  );
                                },
                              )
                            ],
                          ),
                          Container(
                            decoration: BoxDecoration(
                                color: Color.fromRGBO(190, 10, 10, 0.1)),
                            child: Column(children: [
                              Row(
                                children: [
                                  Consumer<ImageProvider>(
                                    builder: (context, imageProvider, child) {
                                      return GestureDetector(
                                        onTap: () {
                                          showModalBottomSheet<void>(
                                            context: context,
                                            builder: (BuildContext context) {
                                              return Container(
                                                height: 200,
                                                color: Colors.white,
                                                child: Center(
                                                  child: Column(
                                                    mainAxisAlignment:
                                                        MainAxisAlignment
                                                            .center,
                                                    mainAxisSize:
                                                        MainAxisSize.min,
                                                    children: <Widget>[
                                                      ListTile(
                                                        leading:
                                                            Icon(Icons.photo),
                                                        title: Text('Gallery'),
                                                        onTap: () async {
                                                          _pickSellImage(
                                                              1, imageProvider);
                                                          Navigator.pop(
                                                              context); // 선택 후 바닥 시트를 닫습니다.
                                                        },
                                                      ),
                                                      ListTile(
                                                        leading:
                                                            Icon(Icons.camera),
                                                        title: Text('Camera'),
                                                        onTap: () async {
                                                          _pickSellImage(
                                                              2, imageProvider);
                                                          Navigator.pop(
                                                              context); // 선택 후 바닥 시트를 닫습니다.
                                                        },
                                                      ),
                                                    ],
                                                  ),
                                                ),
                                              );
                                            },
                                          );
                                        },
                                        child: Container(
                                          decoration: BoxDecoration(
                                            border: Border.all(
                                              color: Color.fromRGBO(
                                                  190, 10, 10, 0.8),
                                              width: 1,
                                            ),
                                            borderRadius:
                                                BorderRadius.circular(20),
                                          ),
                                          padding: EdgeInsets.all(5),
                                          margin: EdgeInsets.all(5),
                                          width: MediaQuery.of(context)
                                                  .size
                                                  .width *
                                              0.35,
                                          height: MediaQuery.of(context)
                                                  .size
                                                  .width *
                                              0.35,
                                          child: imageProvider._imageFile ==
                                                  null
                                              ? Center(
                                                  child: Text('탭하여 이미지 설정'))
                                              : Image.file(File(imageProvider
                                                  ._imageFile!.path)),
                                        ),
                                      );
                                    },
                                  ),
                                  Form(
                                    key: _formKey,
                                    child: Container(
                                      width: MediaQuery.of(context).size.width *
                                          0.6,
                                      height:
                                          MediaQuery.of(context).size.height *
                                              0.2,
                                      child: Column(
                                        children: [
                                          TextFormField(
                                            controller: _productNameController,
                                            decoration: const InputDecoration(
                                                labelText: '상품명'),
                                            validator: (String? value) {
                                              if (value == null ||
                                                  value.isEmpty) {
                                                return '상품명을 입력하세요';
                                              }
                                              return null;
                                            },
                                          ),
                                          TextFormField(
                                            controller: _priceController,
                                            decoration: const InputDecoration(
                                                labelText: '가격'),
                                            obscureText: false,
                                            validator: (String? value) {
                                              if (value == null ||
                                                  value.isEmpty) {
                                                return '가격을 입력하세요';
                                              }
                                              if (int.tryParse(value) == null) {
                                                return '숫자로 입력하세요';
                                              }
                                              return null;
                                            },
                                          ),
                                        ],
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ]),
                          ),
                          Expanded(
                            child: Container(
                              child: quill.QuillEditor.basic(
                                  controller: _quillController,
                                  readOnly: false,
                                  embedBuilders: FlutterQuillEmbeds.builders()),
                            ),
                          ),
                          quill.QuillToolbar.basic(
                            controller: _quillController,
                            showCodeBlock: false,
                            showFontFamily: false,
                            showFontSize: false,
                            showIndent: false,
                            showListCheck: false,
                            showListBullets: false,
                            showListNumbers: false,
                            showSearchButton: false,
                            showQuote: false,
                            showSubscript: false,
                            showInlineCode: false,
                            showSmallButton: false,
                            showDirection: false,
                            showHeaderStyle: false,
                            showLink: false,
                            showDividers: false,
                            showRedo: false,
                            showUndo: false,
                            customButtons: [
                              quill.QuillCustomButton(
                                  child: IconButton(
                                      icon: Icon(Icons.image),
                                      // child: Icon(
                                      //   Icons.image
                                      // ),
                                      onPressed: () {
                                        showModalBottomSheet<void>(
                                          context: context,
                                          builder: (BuildContext context) {
                                            return Container(
                                              height: 200,
                                              color: Colors.white,
                                              child: Center(
                                                child: Column(
                                                  mainAxisAlignment:
                                                      MainAxisAlignment.center,
                                                  mainAxisSize:
                                                      MainAxisSize.min,
                                                  children: <Widget>[
                                                    ListTile(
                                                      leading:
                                                          Icon(Icons.photo),
                                                      title: Text('Gallery'),
                                                      onTap: () async {
                                                        String id =
                                                            await _pickImageAndUpload(
                                                                1);
                                                        if (id != "") {
                                                          insertImage(
                                                              _quillController,
                                                              "${Constants.api_gate_url}/imgfile-service/getimgdata?id=$id");
                                                        }
                                                        Navigator.pop(
                                                            context); // 선택 후 바닥 시트를 닫습니다.
                                                      },
                                                    ),
                                                    ListTile(
                                                      leading:
                                                          Icon(Icons.camera),
                                                      title: Text('Camera'),
                                                      onTap: () async {
                                                        String id =
                                                            await _pickImageAndUpload(
                                                                2);
                                                        if (id != "") {
                                                          insertImage(
                                                              _quillController,
                                                              "${Constants.api_gate_url}/imgfile-service/getimgdata?id=$id");
                                                        }
                                                        Navigator.pop(
                                                            context); // 선택 후 바닥 시트를 닫습니다.
                                                      },
                                                    ),
                                                  ],
                                                ),
                                              ),
                                            );
                                          },
                                        );
                                      }))
                            ],
                          ),
                        ],
                      ),
                    );
                  }
                });
          }
        });
  }
}

class ImageProvider with ChangeNotifier {
  XFile? _imageFile;

  XFile? get imageFile => _imageFile;

  set imageFile(XFile? value) {
    _imageFile = value;
    notifyListeners();
  }
}
