
class ServiceException {
  final int? code;
  final String message;

  ServiceException(this.code, this.message);

  @override
  String toString() => code != null ? " codice: ["+ code!.toString() +"], "+ message : message;
}